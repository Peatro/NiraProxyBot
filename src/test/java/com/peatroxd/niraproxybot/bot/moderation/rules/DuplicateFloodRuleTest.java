package com.peatroxd.niraproxybot.bot.moderation.rules;

import com.peatroxd.niraproxybot.bot.moderation.ModerationContext;
import com.peatroxd.niraproxybot.bot.moderation.ModerationProperties;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationDecision;
import com.peatroxd.niraproxybot.bot.moderation.state.InMemoryMessageFingerprintStore;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateFloodRuleTest {

    @Test
    void flagsSecondDuplicateAndDeletesThirdDuplicate() {
        DuplicateFloodRule rule = new DuplicateFloodRule(new InMemoryMessageFingerprintStore(), moderationProperties());

        assertThat(rule.evaluate(context("same text", "2026-04-15T10:00:00Z")).decision()).isEqualTo(ModerationDecision.ALLOW);
        assertThat(rule.evaluate(context("same text", "2026-04-15T10:00:20Z")).decision()).isEqualTo(ModerationDecision.FLAG);
        assertThat(rule.evaluate(context("same text", "2026-04-15T10:00:40Z")).decision()).isEqualTo(ModerationDecision.DELETE);
    }

    private ModerationContext context(String text, String timestamp) {
        return new ModerationContext(1L, "chat", 10, 42L, "user", text, Instant.parse(timestamp));
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
