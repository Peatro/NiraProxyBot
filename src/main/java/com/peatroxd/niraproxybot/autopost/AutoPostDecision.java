package com.peatroxd.niraproxybot.autopost;

public record AutoPostDecision(
        AutoPostDecisionType type,
        String message
) {

    public static AutoPostDecision posted(String msg) {
        return new AutoPostDecision(AutoPostDecisionType.POSTED, msg);
    }

    public static AutoPostDecision skipped(AutoPostDecisionType type, String msg) {
        return new AutoPostDecision(type, msg);
    }

    public static AutoPostDecision failed(String msg) {
        return new AutoPostDecision(AutoPostDecisionType.FAILED, msg);
    }
}
