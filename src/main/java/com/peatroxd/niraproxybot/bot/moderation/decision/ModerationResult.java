package com.peatroxd.niraproxybot.bot.moderation.decision;

public record ModerationResult(
        ModerationDecision decision,
        ModerationReason reason,
        String details
) {

    public static ModerationResult allow() {
        return new ModerationResult(ModerationDecision.ALLOW, null, "Allowed");
    }

    public static ModerationResult flag(ModerationReason reason, String details) {
        return new ModerationResult(ModerationDecision.FLAG, reason, details);
    }

    public static ModerationResult delete(ModerationReason reason, String details) {
        return new ModerationResult(ModerationDecision.DELETE, reason, details);
    }
}
