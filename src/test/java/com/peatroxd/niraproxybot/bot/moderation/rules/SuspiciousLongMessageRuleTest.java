package com.peatroxd.niraproxybot.bot.moderation.rules;

import com.peatroxd.niraproxybot.bot.moderation.ModerationContext;
import com.peatroxd.niraproxybot.bot.moderation.ModerationProperties;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationDecision;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SuspiciousLongMessageRuleTest {

    @Test
    void flagsLongPromotionalMessageWithLinkAndKeyword() {
        SuspiciousLongMessageRule rule = new SuspiciousLongMessageRule(moderationProperties());
        String text = "promo ".repeat(70) + " write me https://spam.example";

        assertThat(rule.evaluate(context(text)).decision()).isEqualTo(ModerationDecision.FLAG);
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
