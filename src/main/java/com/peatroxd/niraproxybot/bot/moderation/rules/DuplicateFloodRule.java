package com.peatroxd.niraproxybot.bot.moderation.rules;

import com.peatroxd.niraproxybot.bot.moderation.ModerationContext;
import com.peatroxd.niraproxybot.bot.moderation.ModerationProperties;
import com.peatroxd.niraproxybot.bot.moderation.ModerationRule;
import com.peatroxd.niraproxybot.bot.moderation.ModerationTextFeatures;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationReason;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationResult;
import com.peatroxd.niraproxybot.bot.moderation.state.MessageFingerprintStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DuplicateFloodRule implements ModerationRule {

    private final MessageFingerprintStore messageFingerprintStore;
    private final ModerationProperties properties;

    @Override
    public ModerationResult evaluate(ModerationContext context) {
        String normalizedText = ModerationTextFeatures.normalizeText(context.text());
        if (normalizedText.isBlank()) {
            return ModerationResult.allow();
        }

        int repeats = messageFingerprintStore.recordAndCount(
                context.chatId(),
                context.userId(),
                normalizedText,
                context.receivedAt(),
                ModerationTextFeatures.seconds(properties.duplicate().windowSeconds())
        );

        if (repeats >= properties.duplicate().deleteThreshold()) {
            return ModerationResult.delete(
                    ModerationReason.DUPLICATE_FLOOD,
                    "Repeated same text %d times in %ds".formatted(repeats, properties.duplicate().windowSeconds())
            );
        }

        if (repeats >= properties.duplicate().flagThreshold()) {
            return ModerationResult.flag(
                    ModerationReason.DUPLICATE_FLOOD,
                    "Repeated same text %d times in %ds".formatted(repeats, properties.duplicate().windowSeconds())
            );
        }

        return ModerationResult.allow();
    }
}
