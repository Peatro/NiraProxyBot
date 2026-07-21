package com.peatroxd.niraproxybot.autopost;

import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.client.ProxyApiClient;
import com.peatroxd.niraproxybot.dto.HealthStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "health", name = "enabled", havingValue = "true")
public class HealthCheckScheduler {

    private final ProxyApiClient proxyApiClient;
    private final AdminNotifyService adminNotifyService;
    private final NiraBot bot;

    private static final long THROTTLE_MINUTES = 15;

    private Instant lastAlert = Instant.MIN;
    private boolean wasUnhealthy = false;

    @Scheduled(cron = "${health.check-cron}")
    public void tick() {
        HealthStatusDto status = proxyApiClient.fetchHealthStatus();

        // null = сам health-эндпоинт недостижим (бэкенд лежит/сеть). Это тоже проблема.
        boolean healthy = status != null && status.verificationHealthy();

        if (!healthy) {
            boolean throttled = Duration.between(lastAlert, Instant.now()).toMinutes() < THROTTLE_MINUTES;
            if (!throttled) {
                adminNotifyService.notifyHtml(bot, buildAlert(status));
                lastAlert = Instant.now();
            }
            wasUnhealthy = true;
        } else if (wasUnhealthy) {
            adminNotifyService.notifyHtml(bot, "✅ <b>Верификация восстановлена</b>\nEgress: RU, данные свежие.");
            wasUnhealthy = false;
        }
    }

    private String buildAlert(HealthStatusDto s) {
        if (s == null) {
            return "⚠️ <b>Health-эндпоинт недоступен</b>\nБэкенд не отвечает на /health-status.";
        }
        StringBuilder sb = new StringBuilder("⚠️ <b>Проблема верификации</b>\n");
        sb.append("Egress: ").append(s.egressCountry() == null ? "unknown" : s.egressCountry());
        if (!"RU".equals(s.egressCountry())) sb.append(" (не RU!)");
        sb.append("\nПоследняя проверка: ");
        sb.append(s.ageSeconds() == null ? "никогда" : s.ageSeconds() + " сек назад");
        return sb.toString();
    }
}
