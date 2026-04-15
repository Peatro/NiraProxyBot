package com.peatroxd.niraproxybot.autopost;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class PostRateLimitService {

    private final PostStateStore postStateStore;
    private final AutoPostProperties properties;
    private final Clock clock;

    public boolean canPostNow() {
        return postsToday() < properties.maxPostsPerDay();
    }

    public int postsToday() {
        LocalDate today = LocalDate.now(clock);

        return (int) postStateStore.getPostHistory().stream()
                .map(instant -> instant.atZone(clock.getZone()).toLocalDate())
                .filter(today::equals)
                .count();
    }

    public Duration timeUntilNextPostWindow() {
        if (canPostNow()) {
            return Duration.ZERO;
        }

        Instant now = Instant.now(clock);
        ZonedDateTime nextWindow = LocalDate.now(clock)
                .plusDays(1)
                .atStartOfDay(clock.getZone());

        return Duration.between(now, nextWindow.toInstant());
    }
}
