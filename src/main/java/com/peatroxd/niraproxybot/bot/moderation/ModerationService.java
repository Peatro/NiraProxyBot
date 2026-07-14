package com.peatroxd.niraproxybot.bot.moderation;

import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationResult;
import com.peatroxd.niraproxybot.service.AdminAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final ModerationProperties properties;
    private final ModerationRuleEngine moderationRuleEngine;
    private final ModerationActionService moderationActionService;
    private final AdminAccessService adminAccessService;
    private final Clock clock;

    public void handle(NiraBot bot, Update update) {
        if (!properties.enabled() || !shouldModerate(update)) {
            return;
        }

        Message message = update.getMessage();
        Long userId = message.getFrom().getId();
        if (adminAccessService.isAllowed(userId)) {
            return;
        }

        ModerationContext context = new ModerationContext(
                message.getChatId(),
                resolveChatTitle(message),
                message.getMessageId(),
                userId,
                message.getFrom().getUserName(),
                message.getText(),
                Instant.now(clock)
        );

        ModerationResult result = moderationRuleEngine.evaluate(context);
        moderationActionService.apply(bot, context, result);
    }

    private boolean shouldModerate(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }

        Message message = update.getMessage();
        if (message.getChat() == null || message.getFrom() == null) {
            return false;
        }

        String chatType = message.getChat().getType();
        if (!"group".equals(chatType) && !"supergroup".equals(chatType)) {
            return false;
        }

        if (message.getSenderChat() != null) {
            return false;
        }

        return !Boolean.TRUE.equals(message.getFrom().getIsBot());
    }

    private String resolveChatTitle(Message message) {
        if (message.getChat().getTitle() != null && !message.getChat().getTitle().isBlank()) {
            return message.getChat().getTitle();
        }
        return message.getChatId().toString();
    }
}
