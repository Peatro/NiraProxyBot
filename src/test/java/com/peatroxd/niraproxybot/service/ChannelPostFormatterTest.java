package com.peatroxd.niraproxybot.service;

import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelPostFormatterTest {

    private final ChannelPostFormatter formatter = newFormatter();

    private static ChannelPostFormatter newFormatter() {
        ChannelPostFormatter f = new ChannelPostFormatter();
        ReflectionTestUtils.setField(f, "siteUrl", "https://site");
        ReflectionTestUtils.setField(f, "channelUrl", "https://channel");
        ReflectionTestUtils.setField(f, "botUsername", "@NiraProxyBot");
        ReflectionTestUtils.setField(f, "donateUrl", "https://donate");
        return f;
    }

    private static ProxyTelegramLinkDto link(String tgLink) {
        return new ProxyTelegramLinkDto("s", 443, "sec", 200, tgLink);
    }

    @Test
    void emptyListRendersFallbackWithKeyboard() {
        ChannelPost post = formatter.format(List.of());
        assertThat(post.text()).startsWith("🌸 Нира проверила прокси");
        assertThat(post.keyboard().getKeyboard()).hasSize(3);
    }

    @Test
    void nonEmptyPostHasNoQualityLabelsOrMs() {
        ChannelPost post = formatter.format(List.of(link("tg://proxy?a=1")));
        assertThat(post.text())
                .doesNotContain(" ms")
                .doesNotContain("Отличный")
                .doesNotContain("Хороший")
                .doesNotContain("Медленный")
                .doesNotContain("Неизвестно")
                .doesNotContain("🟡")
                .doesNotContain("🔴")
                .doesNotContain("⚪")
                .doesNotContain("⚡");
        assertThat(post.keyboard().getKeyboard()).hasSize(3);
    }

    @Test
    void singleProxyLayoutAndDeclension() {
        ChannelPost post = formatter.format(List.of(link("tg://proxy?a=1&b=2")));
        assertThat(post.text()).isEqualTo("""
                🌸 Нира подобрала свежие MTProto-прокси.
                Все проверены из России и работают прямо сейчас.

                🟢 1 рабочий прокси · проверено только что

                1. 🔗 <a href="tg://proxy?a=1&amp;b=2">Открыть в Telegram</a>

                ↑ Сверху — самый быстрый на момент проверки.""");
    }

    @Test
    void multipleProxiesAreNumberedInApiOrderTopFirst() {
        ChannelPost post = formatter.format(List.of(
                link("tg://first"),
                link("tg://second"),
                link("tg://third")
        ));
        assertThat(post.text())
                .contains("🟢 3 рабочих прокси · проверено только что")
                .contains("1. 🔗 <a href=\"tg://first\">Открыть в Telegram</a>")
                .contains("2. 🔗 <a href=\"tg://second\">Открыть в Telegram</a>")
                .contains("3. 🔗 <a href=\"tg://third\">Открыть в Telegram</a>");
        // порядок сохранён: first раньше second раньше third
        assertThat(post.text().indexOf("tg://first"))
                .isLessThan(post.text().indexOf("tg://second"))
                .isLessThan(post.text().indexOf("tg://third"));
    }

    @Test
    void declensionAcrossCounts() {
        assertThat(workingWord(1)).isEqualTo("рабочий");
        assertThat(workingWord(2)).isEqualTo("рабочих");
        assertThat(workingWord(4)).isEqualTo("рабочих");
        assertThat(workingWord(5)).isEqualTo("рабочих");
        assertThat(workingWord(11)).isEqualTo("рабочих");
        assertThat(workingWord(21)).isEqualTo("рабочий");
        assertThat(workingWord(111)).isEqualTo("рабочих");
    }

    private String workingWord(int n) {
        return (String) ReflectionTestUtils.invokeMethod(formatter, "workingWord", n);
    }
}
