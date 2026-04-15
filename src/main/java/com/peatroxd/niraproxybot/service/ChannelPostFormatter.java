package com.peatroxd.niraproxybot.service;

import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ChannelPostFormatter {

    @Value("${app.site-url}")
    private String siteUrl;

    @Value("${app.channel-url}")
    private String channelUrl;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public String format(List<ProxyTelegramLinkDto> links) {
        List<ProxyTelegramLinkDto> validLinks = links == null
                ? List.of()
                : links.stream()
                .filter(link -> link != null && StringUtils.hasText(link.tgLink()))
                .toList();

        if (validLinks.isEmpty()) {
            return """
                    Нира обновила список, но сейчас нет свежих рабочих прокси.

                    %s
                    """.formatted(footer());
        }

        String body = IntStream.range(0, validLinks.size())
                .mapToObj(index -> formatLink(index + 1, validLinks.get(index)))
                .collect(Collectors.joining("\n\n"));

        return """
                Нира отобрала свежие прокси для Telegram.

                %s

                %s
                """.formatted(body, footer());
    }

    private String formatLink(int index, ProxyTelegramLinkDto link) {
        String tgLink = link.tgLink().trim();
        String latencySuffix = link.latencyMs() != null ? " • " + link.latencyMs() + " ms" : "";
        return "%d. %s%s".formatted(index, escapeHtml(tgLink), latencySuffix);
    }

    private String footer() {
        return """
                <a href="%s">Полный список</a> | <a href="%s">Канал</a> | <a href="%s">Нира</a>
                """.formatted(
                escapeHtml(siteUrl),
                escapeHtml(channelUrl),
                escapeHtml(resolveBotUrl())
        );
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
