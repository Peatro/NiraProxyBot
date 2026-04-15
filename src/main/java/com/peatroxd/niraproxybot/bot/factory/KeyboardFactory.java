package com.peatroxd.niraproxybot.bot.factory;

import com.peatroxd.niraproxybot.bot.CallbackData;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public final class KeyboardFactory {

    private KeyboardFactory() {}

    public static InlineKeyboardMarkup mainMenu(String site, String channel, String chat) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(urlBtn("\uD83D\uDE80 Открыть прокси", site)),
                        List.of(cbBtn("⚡ Лучшие прокси", CallbackData.PROXIES)),
                        List.of(urlBtn("\uD83D\uDCE2 Канал", channel), urlBtn("\uD83D\uDCAC Чат", chat)),
                        List.of(cbBtn("⚠\uFE0F Сообщить о проблеме", CallbackData.BUG))
                ))
                .build();
    }

    public static InlineKeyboardMarkup draftActions() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(
                        cbBtn("✅ Опубликовать", CallbackData.PUBLISH_DRAFT),
                        cbBtn("❌ Отменить", CallbackData.CANCEL_DRAFT)
                )))
                .build();
    }

    public static InlineKeyboardMarkup bugCategories() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(cbBtn(CallbackData.BUG_LABELS.get(CallbackData.BUG_SITE),  CallbackData.BUG_SITE)),
                        List.of(cbBtn(CallbackData.BUG_LABELS.get(CallbackData.BUG_PROXY), CallbackData.BUG_PROXY)),
                        List.of(cbBtn(CallbackData.BUG_LABELS.get(CallbackData.BUG_TG),    CallbackData.BUG_TG)),
                        List.of(cbBtn(CallbackData.BUG_LABELS.get(CallbackData.BUG_OTHER), CallbackData.BUG_OTHER)),
                        List.of(cbBtn("\uD83D\uDD19 Назад", CallbackData.BACK_MAIN))
                ))
                .build();
    }

    private static InlineKeyboardButton urlBtn(String text, String url) {
        return InlineKeyboardButton.builder().text(text).url(url).build();
    }

    private static InlineKeyboardButton cbBtn(String text, String data) {
        return InlineKeyboardButton.builder().text(text).callbackData(data).build();
    }
}
