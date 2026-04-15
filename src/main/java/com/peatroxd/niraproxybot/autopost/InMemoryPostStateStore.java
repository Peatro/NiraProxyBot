package com.peatroxd.niraproxybot.autopost;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class InMemoryPostStateStore implements PostStateStore {

    private static final int MAX_HISTORY_SIZE = 32;

    private final AtomicReference<String> lastFingerprint = new AtomicReference<>();
    private final ConcurrentLinkedDeque<Instant> postHistory = new ConcurrentLinkedDeque<>();

    @Override
    public Optional<String> getLastFingerprint() {
        return Optional.ofNullable(lastFingerprint.get());
    }

    @Override
    public void saveLastFingerprint(String fingerprint) {
        lastFingerprint.set(fingerprint);
    }

    @Override
    public List<Instant> getPostHistory() {
        return List.copyOf(postHistory);
    }

    @Override
    public void recordPostAt(Instant instant) {
        postHistory.addLast(instant);
        trimHistory();
    }

    private void trimHistory() {
        while (postHistory.size() > MAX_HISTORY_SIZE) {
            postHistory.pollFirst();
        }
    }
}
