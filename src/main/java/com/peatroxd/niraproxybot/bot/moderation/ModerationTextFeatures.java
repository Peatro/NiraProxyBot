package com.peatroxd.niraproxybot.bot.moderation;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ModerationTextFeatures {

    private static final Pattern LINK_PATTERN = Pattern.compile(
            "(?i)(https?://\\S+|www\\.\\S+|t\\.me/\\S+|tg://\\S+|\\b(?:[a-z0-9-]+\\.)+[a-z]{2,}(?:/\\S*)?)"
    );
    private static final Set<Character.UnicodeBlock> EMOJI_BLOCKS = Set.of(
            Character.UnicodeBlock.EMOTICONS,
            Character.UnicodeBlock.MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS,
            Character.UnicodeBlock.SUPPLEMENTAL_SYMBOLS_AND_PICTOGRAPHS,
            Character.UnicodeBlock.TRANSPORT_AND_MAP_SYMBOLS,
            Character.UnicodeBlock.MISCELLANEOUS_SYMBOLS,
            Character.UnicodeBlock.DINGBATS
    );

    private ModerationTextFeatures() {
    }

    public static String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        return text
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static List<String> extractSuspiciousLinks(String text, ModerationProperties.LinksProperties properties) {
        List<String> suspiciousLinks = new ArrayList<>();
        Matcher matcher = LINK_PATTERN.matcher(text == null ? "" : text);

        while (matcher.find()) {
            String candidate = sanitizeLink(matcher.group());
            if (!candidate.isEmpty() && !isAllowedLink(candidate, properties)) {
                suspiciousLinks.add(candidate);
            }
        }

        return suspiciousLinks;
    }

    public static int countKeywordMatches(String text, List<String> keywords) {
        if (text == null || text.isBlank() || keywords == null || keywords.isEmpty()) {
            return 0;
        }

        String normalized = normalizeText(text);
        return (int) keywords.stream()
                .filter(normalized::contains)
                .count();
    }

    public static int maxRepeatedCharRun(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        int maxRun = 1;
        int currentRun = 1;
        char previous = text.charAt(0);

        for (int i = 1; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == previous) {
                currentRun++;
                maxRun = Math.max(maxRun, currentRun);
            } else {
                previous = current;
                currentRun = 1;
            }
        }

        return maxRun;
    }

    public static int countEmoji(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        return text.codePoints()
                .mapToObj(Character.UnicodeBlock::of)
                .mapToInt(block -> EMOJI_BLOCKS.contains(block) ? 1 : 0)
                .sum();
    }

    public static double symbolRatio(String text) {
        if (text == null || text.isBlank()) {
            return 0.0d;
        }

        int visibleChars = 0;
        int symbolChars = 0;
        int index = 0;

        while (index < text.length()) {
            int codePoint = text.codePointAt(index);
            if (!Character.isWhitespace(codePoint)) {
                visibleChars++;
                if (!Character.isLetterOrDigit(codePoint)) {
                    symbolChars++;
                }
            }
            index += Character.charCount(codePoint);
        }

        return visibleChars == 0 ? 0.0d : (double) symbolChars / visibleChars;
    }

    public static Duration seconds(int seconds) {
        return Duration.ofSeconds(Math.max(0, seconds));
    }

    private static boolean isAllowedLink(String link, ModerationProperties.LinksProperties properties) {
        String scheme = extractScheme(link);
        if (scheme != null && properties.allowedSchemes().contains(scheme)) {
            return true;
        }

        String host = extractHost(link);
        if (host == null) {
            return false;
        }

        return properties.allowedDomains().stream()
                .anyMatch(allowed -> host.equals(allowed) || host.endsWith("." + allowed));
    }

    private static String sanitizeLink(String link) {
        String sanitized = link == null ? "" : link.trim();
        while (!sanitized.isEmpty() && ".,!?;:)]}>".indexOf(sanitized.charAt(sanitized.length() - 1)) >= 0) {
            sanitized = sanitized.substring(0, sanitized.length() - 1);
        }
        return sanitized;
    }

    private static String extractScheme(String link) {
        int separator = link.indexOf("://");
        if (separator <= 0) {
            return null;
        }
        return link.substring(0, separator).toLowerCase(Locale.ROOT);
    }

    private static String extractHost(String link) {
        try {
            String prepared = link;
            if (prepared.startsWith("www.") || prepared.startsWith("t.me/")) {
                prepared = "https://" + prepared;
            } else if (!prepared.contains("://")) {
                prepared = "https://" + prepared;
            }

            URI uri = URI.create(prepared);
            String host = uri.getHost();
            return host == null ? null : host.toLowerCase(Locale.ROOT);
        } catch (Exception e) {
            return null;
        }
    }
}
