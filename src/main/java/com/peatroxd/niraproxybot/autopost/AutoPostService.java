package com.peatroxd.niraproxybot.autopost;

import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.client.ProxyApiClient;
import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import com.peatroxd.niraproxybot.service.ChannelPostFormatter;
import com.peatroxd.niraproxybot.service.ChannelPostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoPostService {

    private final AutoPostProperties properties;
    private final ProxyApiClient proxyApiClient;
    private final PostFingerprintService fingerprintService;
    private final PostRateLimitService rateLimitService;
    private final PostStateStore postStateStore;
    private final ChannelPostFormatter formatter;
    private final ChannelPostingService channelPostingService;
    private final AdminNotifyService adminNotifyService;
    private final Clock clock;

    public AutoPostDecision run(NiraBot bot) {
        if (!properties.enabled()) {
            return AutoPostDecision.skipped(AutoPostDecisionType.SKIPPED_DISABLED, "Autopost disabled");
        }

        try {
            List<ProxyTelegramLinkDto> links = sanitizeLinks(proxyApiClient.fetchBestLinks(properties.fetchLimit()));
            if (links.isEmpty()) {
                return AutoPostDecision.skipped(AutoPostDecisionType.SKIPPED_EMPTY, "No valid proxy links to post");
            }

            String newFingerprint = fingerprintService.fingerprint(links);
            String oldFingerprint = postStateStore.getLastFingerprint().orElse(null);
            if (newFingerprint.equals(oldFingerprint)) {
                return AutoPostDecision.skipped(AutoPostDecisionType.SKIPPED_DUPLICATE, "Duplicate content");
            }

            if (!rateLimitService.canPostNow()) {
                return AutoPostDecision.skipped(
                        AutoPostDecisionType.SKIPPED_RATE_LIMIT,
                        "Daily limit reached, next window in " + formatDuration(rateLimitService.timeUntilNextPostWindow())
                );
            }

            String text = formatter.format(links);
            if (properties.dryRun()) {
                if (properties.notifyAdminOnPost()) {
                    adminNotifyService.notifyHtml(bot, """
                            [DRY RUN]
                            Был бы опубликован такой пост:

                            %s
                            """.formatted(text));
                }
                return AutoPostDecision.skipped(AutoPostDecisionType.SKIPPED_DRY_RUN, "Dry run mode");
            }

            channelPostingService.postRaw(bot, text);

            Instant postedAt = Instant.now(clock);
            postStateStore.saveLastFingerprint(newFingerprint);
            postStateStore.recordPostAt(postedAt);

            if (properties.notifyAdminOnPost()) {
                adminNotifyService.notify(bot, """
                        [AUTOPOST]
                        Пост опубликован в канал.
                        """);
            }

            return AutoPostDecision.posted("Posted successfully");
        } catch (Exception e) {
            log.error("Autopost failed", e);

            if (properties.notifyAdminOnError()) {
                adminNotifyService.notify(bot, """
                        [AUTOPOST ERROR]
                        %s
                        """.formatted(safeErrorMessage(e)));
            }

            return AutoPostDecision.failed("Autopost failed: " + safeErrorMessage(e));
        }
    }

    private List<ProxyTelegramLinkDto> sanitizeLinks(List<ProxyTelegramLinkDto> links) {
        if (links == null || links.isEmpty()) {
            return List.of();
        }

        LinkedHashMap<String, ProxyTelegramLinkDto> uniqueLinks = new LinkedHashMap<>();
        for (ProxyTelegramLinkDto link : links) {
            if (link == null || !StringUtils.hasText(link.tgLink())) {
                continue;
            }

            String tgLink = link.tgLink().trim();
            uniqueLinks.putIfAbsent(tgLink, new ProxyTelegramLinkDto(
                    link.server(),
                    link.port(),
                    link.secret(),
                    link.latencyMs(),
                    tgLink
            ));
        }

        return List.copyOf(uniqueLinks.values());
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return Math.max(0, minutes) + "m";
    }

    private String safeErrorMessage(Exception e) {
        if (StringUtils.hasText(e.getMessage())) {
            return e.getMessage();
        }
        return e.getClass().getSimpleName();
    }
}
