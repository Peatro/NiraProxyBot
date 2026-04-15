package com.peatroxd.niraproxybot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotInitializer implements CommandLineRunner {

    private final NiraBot niraBot;

    @Override
    public void run(String... args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(niraBot);
            log.info("NiraBot registered successfully");
        } catch (TelegramApiException e) {
            log.error("Failed to register NiraBot", e);
            throw new RuntimeException(e);
        }
    }
}
