package com.peatroxd.niraproxybot.bot.moderation.rules;

import com.peatroxd.niraproxybot.bot.moderation.ModerationContext;
import com.peatroxd.niraproxybot.bot.moderation.ModerationProperties;
import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationDecision;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LinkSpamRuleTest {

    @Test
    void flagsThreeSuspiciousLinksAndIgnoresAllowedOnes() {
        LinkSpamRule rule = new LinkSpamRule(moderationProperties());
        ModerationContext context = context("""
                https://spam-one.example
                https://spam-two.example
                https://spam-three.example
                https://mtproxycheck.ru
                t.me/mtproxycheck
                tg://proxy?server=1.1.1.1
                """);

        assertThat(rule.evaluate(context).decision()).isEqualTo(ModerationDecision.FLAG);
    }

    @Test
    void deletesFiveSuspiciousLinks() {
        LinkSpamRule rule = new LinkSpamRule(moderationProperties());
        ModerationContext context = context("""
                https://spam-one.example
                https://spam-two.example
                https://spam-three.example
                https://spam-four.example
                https://spam-five.example
                """);

        assertThat(rule.evaluate(context).decision()).isEqualTo(ModerationDecision.DELETE);
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
