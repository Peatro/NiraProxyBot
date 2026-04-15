package com.peatroxd.niraproxybot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProxyTelegramLinkDto(
        String server,
        int port,
        String secret,
        Integer latencyMs,
        String tgLink
) {
}
