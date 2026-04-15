package com.peatroxd.niraproxybot.autopost;

import com.peatroxd.niraproxybot.bot.NiraBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "autopost", name = "enabled", havingValue = "true")
public class AutoPostScheduler {

    private final AutoPostService autoPostService;
    private final NiraBot bot;

    @Scheduled(cron = "${autopost.check-cron}")
    public void tick() {
        AutoPostDecision decision = autoPostService.run(bot);
        log.info("Autopost decision: {} - {}", decision.type(), decision.message());
    }
}
