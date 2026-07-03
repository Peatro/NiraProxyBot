package com.peatroxd.niraproxybot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "telegram.bot", name = "startup-enabled", havingValue = "true", matchIfMissing = true)
public class BotInitializer implements CommandLineRunner {

    private final NiraBot niraBot;

    @Override
    public void run(String... args) {
        validateBotCredentials();

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(niraBot);
            log.info("NiraBot registered successfully");
        } catch (TelegramApiException e) {
            log.error("Failed to register NiraBot", e);
            throw new RuntimeException(e);
        }
    }

    private void validateBotCredentials() {
        if (!StringUtils.hasText(niraBot.getBotUsername())) {
            throw new IllegalStateException("""
                    telegram.bot.username is empty.
                    Set a valid bot username in application.yaml or external configuration.
                    """.strip());
        }

        if (!StringUtils.hasText(niraBot.getBotToken())) {
            throw new IllegalStateException("""
                    telegram.bot.token is empty.
                    Set TELEGRAM_BOT_TOKEN in the environment or disable bot startup with telegram.bot.startup-enabled=false.
                    """.strip());
        }
    }
}
