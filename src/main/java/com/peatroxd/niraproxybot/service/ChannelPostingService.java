package com.peatroxd.niraproxybot.service;

import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.client.ProxyApiClient;
import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelPostingService {

    @Value("${telegram.bot.channel-chat-id}")
    private String channelChatId;

    private final ProxyApiClient proxyApiClient;
    private final ChannelPostFormatter formatter;

    public ChannelPost buildDraft(int limit) {
        List<ProxyTelegramLinkDto> links = proxyApiClient.getBestLinks(limit);
        return formatter.format(links);
    }

    public void postToChannel(NiraBot bot, int limit) throws TelegramApiException {
        postRaw(bot, buildDraft(limit));
    }

    public void postRaw(NiraBot bot, ChannelPost post) throws TelegramApiException {
        bot.execute(htmlMessage(channelChatId, post));
    }

    public SendMessage htmlMessage(String chatId, ChannelPost post) {
        SendMessage message = new SendMessage(chatId, post.text());
        message.setParseMode("HTML");
        message.setLinkPreviewOptions(LinkPreviewOptions.builder().isDisabled(true).build());
        message.setReplyMarkup(post.keyboard());
        return message;
    }
}
