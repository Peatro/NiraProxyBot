package com.peatroxd.niraproxybot.autopost;

import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostFingerprintServiceTest {

    private final PostFingerprintService service = new PostFingerprintService();

    @Test
    void sameLinksProduceSameFingerprint() {
        List<ProxyTelegramLinkDto> first = List.of(
                new ProxyTelegramLinkDto("a", 443, "x", 110, "tg://proxy-1"),
                new ProxyTelegramLinkDto("b", 443, "y", 140, "tg://proxy-2")
        );
        List<ProxyTelegramLinkDto> second = List.of(
                new ProxyTelegramLinkDto("other", 8443, "z", 999, "tg://proxy-1"),
                new ProxyTelegramLinkDto("another", 9443, "q", null, "tg://proxy-2")
        );

        assertThat(service.fingerprint(first)).isEqualTo(service.fingerprint(second));
    }

    @Test
    void differentOrderProducesDifferentFingerprint() {
        List<ProxyTelegramLinkDto> first = List.of(
                new ProxyTelegramLinkDto("a", 443, "x", 110, "tg://proxy-1"),
                new ProxyTelegramLinkDto("b", 443, "y", 140, "tg://proxy-2")
        );
        List<ProxyTelegramLinkDto> second = List.of(
                new ProxyTelegramLinkDto("b", 443, "y", 140, "tg://proxy-2"),
                new ProxyTelegramLinkDto("a", 443, "x", 110, "tg://proxy-1")
        );

        assertThat(service.fingerprint(first)).isNotEqualTo(service.fingerprint(second));
    }
}
