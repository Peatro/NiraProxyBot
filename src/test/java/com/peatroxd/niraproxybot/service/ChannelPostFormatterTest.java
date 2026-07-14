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

    @Test
    void emptyListRendersFallbackWithKeyboard() {
        ChannelPost post = formatter.format(List.of());
        assertThat(post.text()).startsWith("🌸 Нира проверила прокси");
        assertThat(post.keyboard().getKeyboard()).hasSize(3);
    }

    @Test
    void proxyBlockCarriesQualityLatencyAndEscapedLink() {
        ChannelPost post = formatter.format(List.of(
                new ProxyTelegramLinkDto("s", 443, "sec", 45, "tg://proxy?a=1&b=2")
        ));
        assertThat(post.text())
                .startsWith("🌸 Нира подобрала свежие MTProto-прокси.")
                .contains("🟢 Отличный")
                .contains("⚡ 45 ms")
                .contains("🔗 <a href=\"tg://proxy?a=1&amp;b=2\">Открыть в Telegram</a>");
    }

    @Test
    void qualityThresholds() {
        assertThat(quality(60)).isEqualTo("🟢 Отличный");
        assertThat(quality(61)).isEqualTo("🟡 Хороший");
        assertThat(quality(120)).isEqualTo("🟡 Хороший");
        assertThat(quality(121)).isEqualTo("🔴 Медленный");
    }

    @Test
    void nullLatencyIsUnknownAndOmitsLatencyLine() {
        ChannelPost post = formatter.format(List.of(
                new ProxyTelegramLinkDto("s", 443, "sec", null, "tg://proxy")
        ));
        assertThat(post.text()).contains("⚪ Неизвестно");
        assertThat(post.text()).doesNotContain("⚡");
    }

    private String quality(Integer latency) {
        return (String) ReflectionTestUtils.invokeMethod(formatter, "quality", latency);
    }
}
