package com.peatroxd.niraproxybot.bot.handler;

import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.bot.moderation.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class UpdateHandler {

    private final CommandHandler commandHandler;
    private final CallbackHandler callbackHandler;
    private final MessageHandler messageHandler;
    private final ModerationService moderationService;

    public void handle(NiraBot bot, Update update) {
        moderationService.handle(bot, update);

        if (isPrivateTextMessage(update)) {
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

    private boolean isPrivateTextMessage(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getChat() != null
                && "private".equals(update.getMessage().getChat().getType());
    }
}
