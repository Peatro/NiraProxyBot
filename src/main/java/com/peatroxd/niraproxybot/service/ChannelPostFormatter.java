package com.peatroxd.niraproxybot.service;

import com.peatroxd.niraproxybot.bot.factory.KeyboardFactory;
import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ChannelPostFormatter {

    // Верно только если пост уходит сразу после прогона планировщика. Если между
    // проверкой и постингом появится лаг — заменить на «проверено сегодня» или
    // убрать, оставив таймстамп самого сообщения Telegram.
    private static final String FRESHNESS = "проверено только что";

    @Value("${app.site-url}")
    private String siteUrl;

    @Value("${app.channel-url}")
    private String channelUrl;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${app.donate-url}")
    private String donateUrl;

    public ChannelPost format(List<ProxyTelegramLinkDto> links) {
        // Порядок API сохраняется как есть: эндпоинт best-links уже ранжирует прокси
        // на сервере по устойчивой метрике, latencyMs (одиночный volatile-замер) для
        // сортировки не годится. Новый список — вход не мутируется.
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

        int count = validLinks.size();
        String list = IntStream.range(0, count)
                .mapToObj(i -> (i + 1) + ". 🔗 <a href=\""
                        + escapeHtml(validLinks.get(i).tgLink().trim())
                        + "\">Открыть в Telegram</a>")
                .collect(Collectors.joining("\n"));

        String text = """
                🌸 Нира подобрала свежие MTProto-прокси.
                Все проверены из России и работают прямо сейчас.

                🟢 %s %s прокси · %s

                %s

                ↑ Сверху — самый быстрый на момент проверки.""".formatted(
                escapeHtml(String.valueOf(count)),
                workingWord(count),
                FRESHNESS,
                list
        );

        return new ChannelPost(text, keyboard);
    }

    // Склонение «рабочий»: 1 рабочий · 2–4 рабочих · 5+ рабочих (с учётом 11–14).
    // ponytail: формы «мало» и «много» для этого слова совпадают («рабочих»),
    // поэтому веток две; если слово сменят на такое, где 2–4 и 5+ различаются
    // (штука/штуки/штук), добавить отдельную ветку для 2–4.
    private String workingWord(int n) {
        int mod100 = n % 100;
        if (n % 10 == 1 && mod100 != 11) {
            return "рабочий";
        }
        return "рабочих";
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
