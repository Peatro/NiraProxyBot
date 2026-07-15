package com.peatroxd.niraproxybot.service;

import com.peatroxd.niraproxybot.bot.factory.KeyboardFactory;
import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChannelPostFormatter {

    @Value("${app.site-url}")
    private String siteUrl;

    @Value("${app.channel-url}")
    private String channelUrl;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${app.donate-url}")
    private String donateUrl;

    public ChannelPost format(List<ProxyTelegramLinkDto> links) {
        List<ProxyTelegramLinkDto> validLinks = links == null
                ? List.of()
                : links.stream()
                .filter(link -> link != null && StringUtils.hasText(link.tgLink()))
                .toList();

        InlineKeyboardMarkup keyboard = KeyboardFactory.channelPost(siteUrl, channelUrl, resolveBotUrl(), donateUrl);

        if (validLinks.isEmpty()) {
            return new ChannelPost("""
                    🌸 Нира проверила прокси, но сейчас свежих рабочих вариантов нет.
                    Попробуйте немного позже — список регулярно обновляется.""", keyboard);
        }

        String body = validLinks.stream()
                .map(this::formatBlock)
                .collect(Collectors.joining("\n\n"));

        String text = """
                🌸 Нира подобрала свежие MTProto-прокси.
                Все варианты уже проверены и готовы к подключению.

                %s""".formatted(body);

        return new ChannelPost(text, keyboard);
    }

    private String formatBlock(ProxyTelegramLinkDto link) {
        StringBuilder block = new StringBuilder(quality(link.latencyMs()));
        if (link.latencyMs() != null) {
            block.append("\n⚡ ").append(link.latencyMs()).append(" ms");
        }
        block.append("\n🔗 <a href=\"").append(escapeHtml(link.tgLink().trim())).append("\">Открыть в Telegram</a>");
        return block.toString();
    }

    private String quality(Integer latency) {
        if (latency == null) {
            return "⚪ Неизвестно";
        }
        if (latency <= 60) {
            return "🟢 Отличный";
        }
        if (latency <= 120) {
            return "🟡 Хороший";
        }
        return "🔴 Медленный";
    }

    private String resolveBotUrl() {
        String username = botUsername == null ? "" : botUsername.trim();
        if (username.startsWith("@")) {
            username = username.substring(1);
        }
        return "https://t.me/" + username;
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
