package com.peatroxd.niraproxybot.bot.moderation.state;

import java.time.Duration;
import java.time.Instant;

public interface MessageFingerprintStore {

    int recordAndCount(Long chatId, Long userId, String normalizedText, Instant timestamp, Duration window);
}
