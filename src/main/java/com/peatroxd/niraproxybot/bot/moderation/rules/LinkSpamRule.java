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
public class LinkSpamRule implements ModerationRule {

    private final ModerationProperties properties;

    @Override
    public ModerationResult evaluate(ModerationContext context) {
        List<String> suspiciousLinks = ModerationTextFeatures.extractSuspiciousLinks(context.text(), properties.links());
        int linkCount = suspiciousLinks.size();

        if (linkCount >= properties.links().deleteThreshold()) {
            return ModerationResult.delete(
                    ModerationReason.LINK_SPAM,
                    "Contains %d suspicious links".formatted(linkCount)
            );
        }

        if (linkCount >= properties.links().flagThreshold()) {
            return ModerationResult.flag(
                    ModerationReason.LINK_SPAM,
                    "Contains %d suspicious links".formatted(linkCount)
            );
        }

        return ModerationResult.allow();
    }
}
