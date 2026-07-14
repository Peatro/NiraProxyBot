package com.peatroxd.niraproxybot.autopost;

import com.peatroxd.niraproxybot.bot.NiraBot;
import com.peatroxd.niraproxybot.client.ProxyApiClient;
import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import com.peatroxd.niraproxybot.service.ChannelPost;
import com.peatroxd.niraproxybot.service.ChannelPostFormatter;
import com.peatroxd.niraproxybot.service.ChannelPostingService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AutoPostServiceTest {

    @Test
    void skipsDuplicateContent() throws Exception {
        AutoPostProperties properties = new AutoPostProperties(true, false, 3, "0 */15 * * * *", 2, true, true);
        ProxyApiClient proxyApiClient = mock(ProxyApiClient.class);
        ChannelPostFormatter formatter = mock(ChannelPostFormatter.class);
        ChannelPostingService channelPostingService = mock(ChannelPostingService.class);
        AdminNotifyService adminNotifyService = mock(AdminNotifyService.class);
        NiraBot bot = mock(NiraBot.class);
        Clock clock = Clock.fixed(Instant.parse("2026-04-15T12:00:00Z"), ZoneId.of("UTC"));
        InMemoryPostStateStore store = new InMemoryPostStateStore();
        PostFingerprintService fingerprintService = new PostFingerprintService();
        PostRateLimitService rateLimitService = new PostRateLimitService(store, properties, clock);
        AutoPostService service = new AutoPostService(
                properties,
                proxyApiClient,
                fingerprintService,
                rateLimitService,
                store,
                formatter,
                channelPostingService,
                adminNotifyService,
                clock
        );

        List<ProxyTelegramLinkDto> links = List.of(
                new ProxyTelegramLinkDto("a", 443, "x", 90, "tg://proxy-1"),
                new ProxyTelegramLinkDto("b", 443, "y", 120, "tg://proxy-2")
        );
        when(proxyApiClient.fetchBestLinks(3)).thenReturn(links);
        store.saveLastFingerprint(fingerprintService.fingerprint(links));

        AutoPostDecision decision = service.run(bot);

        assertThat(decision.type()).isEqualTo(AutoPostDecisionType.SKIPPED_DUPLICATE);
        verifyNoInteractions(formatter, channelPostingService, adminNotifyService);
    }

    @Test
    void postsAndStoresStateWhenAllowed() throws Exception {
        AutoPostProperties properties = new AutoPostProperties(true, false, 3, "0 */15 * * * *", 2, true, true);
        ProxyApiClient proxyApiClient = mock(ProxyApiClient.class);
        ChannelPostFormatter formatter = mock(ChannelPostFormatter.class);
        ChannelPostingService channelPostingService = mock(ChannelPostingService.class);
        AdminNotifyService adminNotifyService = mock(AdminNotifyService.class);
        NiraBot bot = mock(NiraBot.class);
        Clock clock = Clock.fixed(Instant.parse("2026-04-15T12:00:00Z"), ZoneId.of("UTC"));
        InMemoryPostStateStore store = new InMemoryPostStateStore();
        PostFingerprintService fingerprintService = new PostFingerprintService();
        PostRateLimitService rateLimitService = new PostRateLimitService(store, properties, clock);
        AutoPostService service = new AutoPostService(
                properties,
                proxyApiClient,
                fingerprintService,
                rateLimitService,
                store,
                formatter,
                channelPostingService,
                adminNotifyService,
                clock
        );

        List<ProxyTelegramLinkDto> links = List.of(
                new ProxyTelegramLinkDto("a", 443, "x", 90, "tg://proxy-1"),
                new ProxyTelegramLinkDto("b", 443, "y", 120, "tg://proxy-2")
        );
        when(proxyApiClient.fetchBestLinks(3)).thenReturn(links);
        ChannelPost post = new ChannelPost("ready post", null);
        when(formatter.format(links)).thenReturn(post);

        AutoPostDecision decision = service.run(bot);

        assertThat(decision.type()).isEqualTo(AutoPostDecisionType.POSTED);
        assertThat(store.getLastFingerprint()).contains(fingerprintService.fingerprint(links));
        assertThat(store.getPostHistory()).containsExactly(Instant.parse("2026-04-15T12:00:00Z"));
        verify(channelPostingService).postRaw(bot, post);
        verify(adminNotifyService).notify(eq(bot), contains("Пост опубликован"));
        verify(adminNotifyService, never()).notify(eq(bot), contains("[AUTOPOST ERROR]"));
    }
}
