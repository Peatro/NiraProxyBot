package com.peatroxd.niraproxybot.bot.moderation;

import java.time.Instant;

public record ModerationContext(
        Long chatId,
        String chatTitle,
        Integer messageId,
        Long userId,
        String username,
        String text,
        Instant receivedAt
) {

    public String userLabel() {
        return username == null || username.isBlank() ? "(no username)" : "@" + username;
    }
}
