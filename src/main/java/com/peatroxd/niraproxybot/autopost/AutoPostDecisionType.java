package com.peatroxd.niraproxybot.autopost;

public enum AutoPostDecisionType {
    POSTED,
    SKIPPED_DISABLED,
    SKIPPED_DRY_RUN,
    SKIPPED_EMPTY,
    SKIPPED_DUPLICATE,
    SKIPPED_RATE_LIMIT,
    FAILED
}
