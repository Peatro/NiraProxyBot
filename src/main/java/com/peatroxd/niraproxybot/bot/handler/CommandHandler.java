package com.peatroxd.niraproxybot.bot.handler;

import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.bot.factory.KeyboardFactory;
import com.peatroxd.niraproxybot.bot.factory.MessageFactory;
import com.peatroxd.niraproxybot.service.AdminAccessService;
import com.peatroxd.niraproxybot.service.ChannelPostingService;
import com.peatroxd.niraproxybot.service.DraftStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {

    @Value("${spring.application.best-proxies-limit}")
    private int proxyLimit;

    private final MessageFactory messageFactory;
    private final AdminAccessService adminAccessService;
    private final ChannelPostingService channelPostingService;
    private final DraftStorage draftStorage;

    public void handle(NiraBot bot, Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        switch (text) {
            case "/start" -> send(bot, messageFactory.start(chatId));
            case "/post_best" -> {
                if (!adminAccessService.isAllowed(userId)) {
                    send(bot, messageFactory.simple(chatId, "Нет."));
                    return;
                }
                try {
                    channelPostingService.postToChannel(bot, proxyLimit);
                    send(bot, messageFactory.simple(chatId, "Пост отправлен в канал."));
                } catch (TelegramApiException e) {
                    log.error("Failed to post to channel", e);
                    send(bot, messageFactory.simple(chatId, "Не удалось отправить пост в канал."));
                }
            }
            case "/draft_best" -> {
                if (!adminAccessService.isAllowed(userId)) {
                    send(bot, messageFactory.simple(chatId, "Нет."));
                    return;
                }
                String draft = channelPostingService.buildDraft(proxyLimit);
                draftStorage.save(userId, draft);

                SendMessage msg = channelPostingService.htmlMessage(chatId.toString(), draft);
                msg.setReplyMarkup(KeyboardFactory.draftActions());
                send(bot, msg);
            }
        }
    }

    private void send(NiraBot bot, SendMessage msg) {
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId={}", msg.getChatId(), e);
        }
    }
}
