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
public class KeywordSpamRule implements ModerationRule {

    private final ModerationProperties properties;

    @Override
    public ModerationResult evaluate(ModerationContext context) {
        if (!properties.keywordSpam().enabled()) {
            return ModerationResult.allow();
        }

        int matches = ModerationTextFeatures.countKeywordMatches(context.text(), properties.keywordSpam().keywords());
        if (matches >= 2) {
            return ModerationResult.delete(
                    ModerationReason.KEYWORD_SPAM,
                    "Matched %d spam keywords".formatted(matches)
            );
        }

        if (matches >= 1) {
            return ModerationResult.flag(
                    ModerationReason.KEYWORD_SPAM,
                    "Matched %d spam keyword".formatted(matches)
            );
        }

        return ModerationResult.allow();
    }
}
