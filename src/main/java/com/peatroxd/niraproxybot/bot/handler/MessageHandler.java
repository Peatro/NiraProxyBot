package com.peatroxd.niraproxybot.bot.handler;

import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.bot.factory.MessageFactory;
import com.peatroxd.niraproxybot.enums.UserState;
import com.peatroxd.niraproxybot.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SessionService sessionService;
    private final MessageFactory messageFactory;

    @Value("${telegram.bot.admin-chat-id}")
    private String adminChatId;

    public void handle(NiraBot bot, Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        var session = sessionService.getSession(userId);

        if (session.getState() == UserState.WAITING_BUG_TEXT) {
            String username = update.getMessage().getFrom().getUserName();
            String report = """
                    [BUG]

                    Тип: %s
                    Пользователь: @%s
                    ID: %d

                    Сообщение:
                    %s

                    Время: %s""".formatted(
                    session.getBugType(),
                    username != null ? username : "—",
                    userId,
                    text,
                    LocalDateTime.now().format(FORMATTER)
            );

            send(bot, new SendMessage(adminChatId, report));
            send(bot, new SendMessage(chatId.toString(),
                    "Приняла.\n\nЕсли проблема массовая — поправим быстро."));
            send(bot, messageFactory.start(chatId));
            sessionService.reset(userId);
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
