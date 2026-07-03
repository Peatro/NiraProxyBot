package com.peatroxd.niraproxybot.bot.moderation.state;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryMessageFingerprintStore implements MessageFingerprintStore {

    private final ConcurrentHashMap<UserChatKey, MessageFingerprintState> states = new ConcurrentHashMap<>();

    @Override
    public int recordAndCount(Long chatId, Long userId, String normalizedText, Instant timestamp, Duration window) {
        UserChatKey key = new UserChatKey(chatId, userId);

        MessageFingerprintState state = states.compute(key, (ignored, current) -> {
            if (current == null) {
                return new MessageFingerprintState(normalizedText, timestamp, 1);
            }

            boolean sameText = current.normalizedText().equals(normalizedText);
            boolean inWindow = !timestamp.isAfter(current.timestamp().plus(window));
            if (sameText && inWindow) {
                return new MessageFingerprintState(normalizedText, timestamp, current.repeatCount() + 1);
            }

            return new MessageFingerprintState(normalizedText, timestamp, 1);
        });

        return state.repeatCount();
    }

    private record UserChatKey(Long chatId, Long userId) {
    }

    private record MessageFingerprintState(String normalizedText, Instant timestamp, int repeatCount) {
    }
}
