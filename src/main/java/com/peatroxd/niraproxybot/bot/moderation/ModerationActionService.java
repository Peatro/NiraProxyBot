package com.peatroxd.niraproxybot.bot.moderation;

import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationDecision;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationActionService {

    private final ModerationProperties properties;
    private final ModerationLogService moderationLogService;

    public void apply(NiraBot bot, ModerationContext context, ModerationResult result) {
        if (result.decision() == ModerationDecision.ALLOW) {
            return;
        }

        if (result.decision() == ModerationDecision.FLAG) {
            if (properties.notifyAdminChat()) {
                moderationLogService.logFlag(bot, context, result, null);
            }
            return;
        }

        if (!properties.deleteObviousSpam()) {
            if (properties.notifyAdminChat()) {
                moderationLogService.logFlag(bot, context, result, "Delete disabled by configuration");
            }
            return;
        }

        try {
            bot.execute(new DeleteMessage(context.chatId().toString(), context.messageId()));
            if (properties.notifyAdminChat()) {
                moderationLogService.logDelete(bot, context, result, null);
            }
        } catch (Exception e) {
            log.warn("Failed to delete spam message chatId={} messageId={}", context.chatId(), context.messageId(), e);
            if (properties.notifyAdminChat()) {
                moderationLogService.logFlag(bot, context, result, "Delete failed: " + e.getClass().getSimpleName());
            }
        }
    }
}
