package com.peatroxd.niraproxybot.bot.handler;

import com.peatroxd.niraproxybot.bot.CallbackData;
import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.bot.factory.MessageFactory;
import com.peatroxd.niraproxybot.client.ProxyApiClient;
import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import com.peatroxd.niraproxybot.enums.UserState;
import com.peatroxd.niraproxybot.service.AdminAccessService;
import com.peatroxd.niraproxybot.service.ChannelPost;
import com.peatroxd.niraproxybot.service.ChannelPostingService;
import com.peatroxd.niraproxybot.service.DraftStorage;
import com.peatroxd.niraproxybot.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CallbackHandler {

    @Value("${spring.application.best-proxies-limit}")
    private int proxyLimit;

    private final SessionService sessionService;
    private final MessageFactory messageFactory;
    private final ProxyApiClient proxyApiClient;
    private final AdminAccessService adminAccessService;
    private final ChannelPostingService channelPostingService;
    private final DraftStorage draftStorage;

    public void handle(NiraBot bot, Update update) {
        var callback = update.getCallbackQuery();
        String data = callback.getData();
        Long userId = callback.getFrom().getId();
        Long chatId = callback.getMessage().getChatId();

        switch (data) {
            case CallbackData.PROXIES       -> handleProxies(bot, chatId);
            case CallbackData.PUBLISH_DRAFT -> handlePublish(bot, userId, chatId);
            case CallbackData.CANCEL_DRAFT  -> {
                draftStorage.clear(userId);
                send(bot, new SendMessage(chatId.toString(), "Отменено."));
            }
            case CallbackData.BUG -> {
                sessionService.reset(userId);
                send(bot, messageFactory.bugCategories(chatId));
            }
            case CallbackData.BACK_MAIN -> {
                sessionService.reset(userId);
                send(bot, messageFactory.start(chatId));
            }
            default -> {
                if (CallbackData.BUG_LABELS.containsKey(data)) {
                    var session = sessionService.getSession(userId);
                    session.setBugType(CallbackData.BUG_LABELS.get(data));
                    session.setState(UserState.WAITING_BUG_TEXT);
                    send(bot, new SendMessage(chatId.toString(), "Опиши проблему подробнее."));
                }
            }
        }
    }

    private void handlePublish(NiraBot bot, Long userId, Long chatId) {
        if (!adminAccessService.isAllowed(userId)) {
            send(bot, new SendMessage(chatId.toString(), "Нет."));
            return;
        }

        ChannelPost draft = draftStorage.get(userId);
        if (draft == null) {
            send(bot, new SendMessage(chatId.toString(), "Черновик не найден. Создай новый через /draft_best."));
            return;
        }

        try {
            channelPostingService.postRaw(bot, draft);
            send(bot, new SendMessage(chatId.toString(), "Опубликовано."));
            draftStorage.clear(userId);
        } catch (TelegramApiException e) {
            log.error("Failed to publish draft to channel", e);
            send(bot, new SendMessage(chatId.toString(), "Ошибка при публикации."));
        }
    }

    private void handleProxies(NiraBot bot, Long chatId) {
        List<ProxyTelegramLinkDto> links = proxyApiClient.getBestLinks(proxyLimit);

        String text = links.isEmpty()
                ? "Сервис временно недоступен. Попробуй позже."
                : "Новые ссылки на прокси:\n\n" + links.stream()
                        .map(ProxyTelegramLinkDto::tgLink)
                        .collect(Collectors.joining("\n"));

        send(bot, new SendMessage(chatId.toString(), text));
    }

    private void send(NiraBot bot, SendMessage msg) {
        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId={}", msg.getChatId(), e);
        }
    }
}
