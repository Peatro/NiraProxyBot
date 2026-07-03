package com.peatroxd.niraproxybot.bot.moderation;

import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationDecision;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationResult;
import com.peatroxd.niraproxybot.bot.moderation.rules.DuplicateFloodRule;
import com.peatroxd.niraproxybot.bot.moderation.rules.KeywordSpamRule;
import com.peatroxd.niraproxybot.bot.moderation.rules.LinkSpamRule;
import com.peatroxd.niraproxybot.bot.moderation.rules.SuspiciousLongMessageRule;
import com.peatroxd.niraproxybot.bot.moderation.rules.SymbolSpamRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModerationRuleEngine {

    private final DuplicateFloodRule duplicateFloodRule;
    private final LinkSpamRule linkSpamRule;
    private final KeywordSpamRule keywordSpamRule;
    private final SymbolSpamRule symbolSpamRule;
    private final SuspiciousLongMessageRule suspiciousLongMessageRule;

    public ModerationResult evaluate(ModerationContext context) {
        ModerationResult flaggedResult = ModerationResult.allow();

        for (ModerationRule rule : orderedRules()) {
            ModerationResult result = rule.evaluate(context);
            if (result.decision() == ModerationDecision.DELETE) {
                return result;
            }
            if (result.decision() == ModerationDecision.FLAG && flaggedResult.decision() == ModerationDecision.ALLOW) {
                flaggedResult = result;
            }
        }

        return flaggedResult;
    }

    private List<ModerationRule> orderedRules() {
        return List.of(
                duplicateFloodRule,
                linkSpamRule,
                keywordSpamRule,
                symbolSpamRule,
                suspiciousLongMessageRule
        );
    }
}
