package com.peatroxd.niraproxybot.autopost;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class PostRateLimitServiceTest {

    @Test
    void blocksAfterTwoPostsInSameDay() {
        Clock clock = Clock.fixed(Instant.parse("2026-04-15T12:00:00Z"), ZoneId.of("UTC"));
        AutoPostProperties properties = new AutoPostProperties(true, false, 3, "0 */15 * * * *", 2, true, true);
        InMemoryPostStateStore store = new InMemoryPostStateStore();
        store.recordPostAt(Instant.parse("2026-04-15T01:00:00Z"));
        store.recordPostAt(Instant.parse("2026-04-15T05:00:00Z"));

        PostRateLimitService service = new PostRateLimitService(store, properties, clock);

        assertThat(service.canPostNow()).isFalse();
        assertThat(service.postsToday()).isEqualTo(2);
        assertThat(service.timeUntilNextPostWindow()).isEqualTo(Duration.ofHours(12));
    }

    @Test
    void ignoresPostsFromPreviousDay() {
        Clock clock = Clock.fixed(Instant.parse("2026-04-15T12:00:00Z"), ZoneId.of("UTC"));
        AutoPostProperties properties = new AutoPostProperties(true, false, 3, "0 */15 * * * *", 2, true, true);
        InMemoryPostStateStore store = new InMemoryPostStateStore();
        store.recordPostAt(Instant.parse("2026-04-14T23:00:00Z"));
        store.recordPostAt(Instant.parse("2026-04-15T05:00:00Z"));

        PostRateLimitService service = new PostRateLimitService(store, properties, clock);

        assertThat(service.canPostNow()).isTrue();
        assertThat(service.postsToday()).isEqualTo(1);
        assertThat(service.timeUntilNextPostWindow()).isZero();
    }
}
