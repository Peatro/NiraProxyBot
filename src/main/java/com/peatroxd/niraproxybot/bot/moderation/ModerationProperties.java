package com.peatroxd.niraproxybot.bot.moderation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@ConfigurationProperties(prefix = "moderation")
public record ModerationProperties(
        boolean enabled,
        boolean notifyAdminChat,
        boolean deleteObviousSpam,
        DuplicateProperties duplicate,
        LinksProperties links,
        SymbolSpamProperties symbolSpam,
        KeywordSpamProperties keywordSpam,
        SuspiciousLongMessageProperties suspiciousLongMessage
) {

    public ModerationProperties {
        duplicate = duplicate == null ? new DuplicateProperties(2, 3, 120) : duplicate;
        links = links == null ? new LinksProperties(3, 5, List.of(), List.of()) : normalizeLinks(links);
        symbolSpam = symbolSpam == null ? new SymbolSpamProperties(true, 12, 10) : symbolSpam;
        keywordSpam = keywordSpam == null ? new KeywordSpamProperties(true, List.of()) : normalizeKeywords(keywordSpam);
        suspiciousLongMessage = suspiciousLongMessage == null
                ? new SuspiciousLongMessageProperties(true, 300)
                : suspiciousLongMessage;
    }

    private static LinksProperties normalizeLinks(LinksProperties links) {
        return new LinksProperties(
                links.flagThreshold(),
                links.deleteThreshold(),
                normalizeList(links.allowedDomains()),
                normalizeList(links.allowedSchemes())
        );
    }

    private static KeywordSpamProperties normalizeKeywords(KeywordSpamProperties keywordSpam) {
        return new KeywordSpamProperties(keywordSpam.enabled(), normalizeList(keywordSpam.keywords()));
    }

    private static List<String> normalizeList(List<String> values) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .toList();
    }

    public record DuplicateProperties(
            int flagThreshold,
            int deleteThreshold,
            int windowSeconds
    ) {
    }

    public record LinksProperties(
            int flagThreshold,
            int deleteThreshold,
            List<String> allowedDomains,
            List<String> allowedSchemes
    ) {
    }

    public record SymbolSpamProperties(
            boolean enabled,
            int repeatedCharThreshold,
            int emojiThreshold
    ) {
    }

    public record KeywordSpamProperties(
            boolean enabled,
            List<String> keywords
    ) {
    }

    public record SuspiciousLongMessageProperties(
            boolean enabled,
            int minLength
    ) {
    }
}
