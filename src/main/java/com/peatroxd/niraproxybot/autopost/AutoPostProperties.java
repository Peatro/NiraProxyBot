package com.peatroxd.niraproxybot.autopost;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "autopost")
public record AutoPostProperties(
        boolean enabled,
        boolean dryRun,
        int fetchLimit,
        String checkCron,
        int maxPostsPerDay,
        boolean notifyAdminOnPost,
        boolean notifyAdminOnError
) {

    public AutoPostProperties {
        if (fetchLimit < 1) {
            throw new IllegalArgumentException("autopost.fetch-limit must be greater than zero");
        }
        if (maxPostsPerDay < 1) {
            throw new IllegalArgumentException("autopost.max-posts-per-day must be greater than zero");
        }
    }
}
