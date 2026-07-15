package com.peatroxd.niraproxybot.bot;

import com.peatroxd.niraproxybot.bot.handler.UpdateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

// ponytail: startup-enabled gates the whole bean; when off (tests) the starter has no SpringLongPollingBot to register.
@Component
@ConditionalOnProperty(prefix = "telegram.bot", name = "startup-enabled", havingValue = "true", matchIfMissing = true)
public class NiraBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String token;
    private final UpdateHandler updateHandler;
    private final TelegramClient telegramClient;

    public NiraBot(@Value("${telegram.bot.token}") String token, UpdateHandler updateHandler) {
        this.token = token;
        this.updateHandler = updateHandler;
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        updateHandler.handle(this, update);
    }

    // Passthrough so existing bot.execute(...) call sites stay untouched by the migration.
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        return telegramClient.execute(method);
    }
}
