package com.peatroxd.niraproxybot.bot.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class MessageFactory {

    @Value("${app.site-url}")
    private String siteUrl;

    @Value("${app.channel-url}")
    private String channelUrl;

    @Value("${app.chat-url}")
    private String chatUrl;

    public SendMessage start(Long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText("""
                Нира на связи.
                
                Нужен быстрый доступ к Telegram?
                Я уже подобрала рабочие прокси — можно зайти в один клик.
                
                Выбирай:
                
                Если что-то не открылось — напиши, посмотрю.""");
        msg.setReplyMarkup(KeyboardFactory.mainMenu(siteUrl, channelUrl, chatUrl));
        return msg;
    }

    public SendMessage bugCategories(Long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText("Что именно не работает?");
        msg.setReplyMarkup(KeyboardFactory.bugCategories());
        return msg;
    }

    public SendMessage simple(Long chatId, String text) {
        return new SendMessage(chatId.toString(), text);
    }
}
