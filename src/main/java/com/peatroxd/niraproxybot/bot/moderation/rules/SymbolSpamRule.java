package com.peatroxd.niraproxybot.bot.moderation.rules;

import com.peatroxd.niraproxybot.bot.moderation.ModerationContext;
import com.peatroxd.niraproxybot.bot.moderation.ModerationProperties;
import com.peatroxd.niraproxybot.bot.moderation.ModerationRule;
import com.peatroxd.niraproxybot.bot.moderation.ModerationTextFeatures;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationReason;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SymbolSpamRule implements ModerationRule {

    private final ModerationProperties properties;

    @Override
    public ModerationResult evaluate(ModerationContext context) {
        if (!properties.symbolSpam().enabled()) {
            return ModerationResult.allow();
        }

        int repeatedCharRun = ModerationTextFeatures.maxRepeatedCharRun(context.text());
        int emojiCount = ModerationTextFeatures.countEmoji(context.text());
        double symbolRatio = ModerationTextFeatures.symbolRatio(context.text());

        if (repeatedCharRun >= properties.symbolSpam().repeatedCharThreshold()
                || emojiCount >= properties.symbolSpam().emojiThreshold() * 2
                || (context.text().length() >= 20 && symbolRatio >= 0.85d)) {
            return ModerationResult.delete(
                    ModerationReason.SYMBOL_SPAM,
                    "Repeated chars=%d, emoji=%d, symbol ratio=%.2f".formatted(repeatedCharRun, emojiCount, symbolRatio)
            );
        }

        if (repeatedCharRun >= Math.max(6, properties.symbolSpam().repeatedCharThreshold() / 2)
                || emojiCount >= properties.symbolSpam().emojiThreshold()
                || (context.text().length() >= 20 && symbolRatio >= 0.65d)) {
            return ModerationResult.flag(
                    ModerationReason.SYMBOL_SPAM,
                    "Repeated chars=%d, emoji=%d, symbol ratio=%.2f".formatted(repeatedCharRun, emojiCount, symbolRatio)
            );
        }

        return ModerationResult.allow();
    }
}
