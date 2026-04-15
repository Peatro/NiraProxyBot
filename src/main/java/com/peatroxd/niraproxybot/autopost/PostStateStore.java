package com.peatroxd.niraproxybot.autopost;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PostStateStore {

    Optional<String> getLastFingerprint();

    void saveLastFingerprint(String fingerprint);

    List<Instant> getPostHistory();

    void recordPostAt(Instant instant);
}
