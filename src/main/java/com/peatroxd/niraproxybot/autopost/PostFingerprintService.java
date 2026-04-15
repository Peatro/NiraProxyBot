package com.peatroxd.niraproxybot.autopost;

import com.peatroxd.niraproxybot.dto.ProxyTelegramLinkDto;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostFingerprintService {

    public String fingerprint(List<ProxyTelegramLinkDto> links) {
        String canonical = links.stream()
                .map(ProxyTelegramLinkDto::tgLink)
                .collect(Collectors.joining("\n"));

        return sha256(canonical);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate fingerprint", e);
        }
    }
}
