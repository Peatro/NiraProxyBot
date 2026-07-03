package com.peatroxd.niraproxybot.bot.moderation;

import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@Service
public class ModerationLogService {

    private static final int MAX_TEXT_LENGTH = 1500;

    @Value("${telegram.bot.admin-chat-id}")
    private String adminChatId;

    public void logFlag(NiraBot bot, ModerationContext context, ModerationResult result, String note) {
        send(bot, "MOD FLAG", context, result, note);
    }

    public void logDelete(NiraBot bot, ModerationContext context, ModerationResult result, String note) {
        send(bot, "MOD DELETE", context, result, note);
    }

    private void send(NiraBot bot, String title, ModerationContext context, ModerationResult result, String note) {
        String noteLine = note == null || note.isBlank() ? "" : "Note: " + note + "\n";
        String text = """
                [%s]

                Reason: %s
                User: %s
                User ID: %d
                Chat: %s
                Details: %s
                %sMessage:
                %s
                """.formatted(
                title,
                result.reason(),
                context.userLabel(),
                context.userId(),
                context.chatTitle(),
                result.details(),
                noteLine,
                truncate(context.text())
        );

        try {
            bot.execute(new SendMessage(adminChatId, text));
        } catch (Exception e) {
            log.warn("Failed to send moderation log for userId={}", context.userId(), e);
        }
    }

    private String truncate(String text) {
        if (text == null || text.length() <= MAX_TEXT_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_TEXT_LENGTH) + "...";
    }
}
