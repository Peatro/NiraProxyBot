package com.peatroxd.niraproxybot.bot.moderation.rules;

import com.peatroxd.niraproxybot.bot.moderation.ModerationContext;
import com.peatroxd.niraproxybot.bot.moderation.ModerationProperties;
import com.peatroxd.niraproxybot.bot.moderation.ModerationRule;
import com.peatroxd.niraproxybot.bot.moderation.ModerationTextFeatures;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationReason;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SuspiciousLongMessageRule implements ModerationRule {

    private final ModerationProperties properties;

    @Override
    public ModerationResult evaluate(ModerationContext context) {
        if (!properties.suspiciousLongMessage().enabled()) {
            return ModerationResult.allow();
        }

        if (context.text() == null || context.text().length() < properties.suspiciousLongMessage().minLength()) {
            return ModerationResult.allow();
        }

        List<String> suspiciousLinks = ModerationTextFeatures.extractSuspiciousLinks(context.text(), properties.links());
        int keywordMatches = ModerationTextFeatures.countKeywordMatches(context.text(), properties.keywordSpam().keywords());
        if (!suspiciousLinks.isEmpty() && keywordMatches > 0) {
            return ModerationResult.flag(
                    ModerationReason.SUSPICIOUS_LONG_MESSAGE,
                    "Long message with %d suspicious links and %d spam keywords".formatted(suspiciousLinks.size(), keywordMatches)
            );
        }

        return ModerationResult.allow();
    }
}
