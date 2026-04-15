package com.peatroxd.niraproxybot.bot.handler;

import com.peatroxd.niraproxybot.bot.NiraBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class UpdateHandler {

    private final CommandHandler commandHandler;
    private final CallbackHandler callbackHandler;
    private final MessageHandler messageHandler;

    public void handle(NiraBot bot, Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (text.startsWith("/")) {
                commandHandler.handle(bot, update);
            } else {
                messageHandler.handle(bot, update);
            }
        }

        if (update.hasCallbackQuery()) {
            callbackHandler.handle(bot, update);
        }
    }
}
