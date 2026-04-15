package com.peatroxd.niraproxybot.bot;

import java.util.Map;

public final class CallbackData {

    public static final String PROXIES       = "PROXIES";
    public static final String BUG           = "BUG";
    public static final String BACK_MAIN     = "BACK_MAIN";
    public static final String PUBLISH_DRAFT = "PUBLISH_DRAFT";
    public static final String CANCEL_DRAFT  = "CANCEL_DRAFT";

    public static final String BUG_SITE  = "BUG_SITE";
    public static final String BUG_PROXY = "BUG_PROXY";
    public static final String BUG_TG    = "BUG_TG";
    public static final String BUG_OTHER = "BUG_OTHER";

    public static final Map<String, String> BUG_LABELS = Map.of(
            BUG_SITE,  "\uD83C\uDF10 Сайт не открывается",
            BUG_PROXY, "\uD83D\uDD0C Прокси не работает",
            BUG_TG,    "\uD83D\uDCF1 Telegram не запускается",
            BUG_OTHER, "❓ Другое"
    );

    private CallbackData() {}
}
