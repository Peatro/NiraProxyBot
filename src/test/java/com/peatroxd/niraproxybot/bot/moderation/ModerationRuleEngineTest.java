package com.peatroxd.niraproxybot.bot.moderation;

import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationDecision;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationReason;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationResult;
import com.peatroxd.niraproxybot.bot.moderation.rules.DuplicateFloodRule;
import com.peatroxd.niraproxybot.bot.moderation.rules.KeywordSpamRule;
import com.peatroxd.niraproxybot.bot.moderation.rules.LinkSpamRule;
import com.peatroxd.niraproxybot.bot.moderation.rules.SuspiciousLongMessageRule;
import com.peatroxd.niraproxybot.bot.moderation.rules.SymbolSpamRule;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ModerationRuleEngineTest {

    @Test
    void stopsEvaluationAfterDelete() {
        DuplicateFloodRule duplicateFloodRule = mock(DuplicateFloodRule.class);
        LinkSpamRule linkSpamRule = mock(LinkSpamRule.class);
        KeywordSpamRule keywordSpamRule = mock(KeywordSpamRule.class);
        SymbolSpamRule symbolSpamRule = mock(SymbolSpamRule.class);
        SuspiciousLongMessageRule suspiciousLongMessageRule = mock(SuspiciousLongMessageRule.class);
        ModerationRuleEngine engine = new ModerationRuleEngine(
                duplicateFloodRule,
                linkSpamRule,
                keywordSpamRule,
                symbolSpamRule,
                suspiciousLongMessageRule
        );
        ModerationContext context = new ModerationContext(1L, "chat", 1, 1L, "user", "spam", Instant.now());

        when(duplicateFloodRule.evaluate(context)).thenReturn(ModerationResult.allow());
        when(linkSpamRule.evaluate(context)).thenReturn(ModerationResult.delete(ModerationReason.LINK_SPAM, "delete"));

        ModerationResult result = engine.evaluate(context);

        assertThat(result.decision()).isEqualTo(ModerationDecision.DELETE);
        verify(duplicateFloodRule).evaluate(context);
        verify(linkSpamRule).evaluate(context);
        verifyNoInteractions(keywordSpamRule, symbolSpamRule, suspiciousLongMessageRule);
    }
}
