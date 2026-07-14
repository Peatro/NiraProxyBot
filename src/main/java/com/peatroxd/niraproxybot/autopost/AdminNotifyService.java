package com.peatroxd.niraproxybot.autopost;

import com.peatroxd.niraproxybot.bot.NiraBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;

@Slf4j
@Service
public class AdminNotifyService {

    @Value("${telegram.bot.admin-chat-id}")
    private String adminChatId;

    public void notify(NiraBot bot, String text) {
        send(bot, text, null);
    }

    public void notifyHtml(NiraBot bot, String text) {
        send(bot, text, "HTML");
    }

    private void send(NiraBot bot, String text, String parseMode) {
        try {
            SendMessage message = new SendMessage(adminChatId, text);
            if (parseMode != null) {
                message.setParseMode(parseMode);
                message.setLinkPreviewOptions(LinkPreviewOptions.builder().isDisabled(true).build());
            }
            bot.execute(message);
        } catch (Exception e) {
            log.warn("Failed to notify admin chat {}", adminChatId, e);
        }
    }
}
