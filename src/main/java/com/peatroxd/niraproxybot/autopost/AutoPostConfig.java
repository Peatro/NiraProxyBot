package com.peatroxd.niraproxybot.autopost;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(AutoPostProperties.class)
public class AutoPostConfig {

    @Bean
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }
}
