package com.peatroxd.niraproxybot.bot.moderation.rules;

import com.peatroxd.niraproxybot.bot.moderation.ModerationContext;
import com.peatroxd.niraproxybot.bot.moderation.ModerationProperties;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationDecision;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordAndSymbolRuleTest {

    @Test
    void flagsSingleKeywordSpamMatch() {
        KeywordSpamRule rule = new KeywordSpamRule(moderationProperties());

        assertThat(rule.evaluate(context("write me for details")).decision()).isEqualTo(ModerationDecision.FLAG);
    }

    @Test
    void deletesObviousSymbolSpam() {
        SymbolSpamRule rule = new SymbolSpamRule(moderationProperties());

        assertThat(rule.evaluate(context("!!!!!!!!!!!!!!!!!")).decision()).isEqualTo(ModerationDecision.DELETE);
    }

    private ModerationContext context(String text) {
        return new ModerationContext(1L, "chat", 10, 42L, "user", text, Instant.parse("2026-04-15T10:00:00Z"));
    }

    private ModerationProperties moderationProperties() {
        return new ModerationProperties(
                true,
                true,
                false,
                new ModerationProperties.DuplicateProperties(2, 3, 120),
                new ModerationProperties.LinksProperties(3, 5, List.of("mtproxycheck.ru", "t.me"), List.of("tg")),
                new ModerationProperties.SymbolSpamProperties(true, 12, 10),
                new ModerationProperties.KeywordSpamProperties(true, List.of("casino", "write me")),
                new ModerationProperties.SuspiciousLongMessageProperties(true, 300)
        );
    }
}
