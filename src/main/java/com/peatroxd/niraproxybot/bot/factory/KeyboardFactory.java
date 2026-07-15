package com.peatroxd.niraproxybot.bot.factory;

import com.peatroxd.niraproxybot.bot.CallbackData;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public final class KeyboardFactory {

    private KeyboardFactory() {}

    public static InlineKeyboardMarkup mainMenu(String site, String channel, String chat) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        new InlineKeyboardRow(urlBtn("🚀 Открыть прокси", site)),
                        new InlineKeyboardRow(cbBtn("⚡ Лучшие прокси", CallbackData.PROXIES)),
                        new InlineKeyboardRow(urlBtn("📢 Канал", channel), urlBtn("💬 Чат", chat)),
                        new InlineKeyboardRow(cbBtn("⚠️ Сообщить о проблеме", CallbackData.BUG))
                ))
                .build();
    }

    public static InlineKeyboardMarkup draftActions() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(new InlineKeyboardRow(
                        cbBtn("✅ Опубликовать", CallbackData.PUBLISH_DRAFT),
                        cbBtn("❌ Отменить", CallbackData.CANCEL_DRAFT)
                )))
                .build();
    }

    public static InlineKeyboardMarkup bugCategories() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        new InlineKeyboardRow(cbBtn(CallbackData.BUG_LABELS.get(CallbackData.BUG_SITE),  CallbackData.BUG_SITE)),
                        new InlineKeyboardRow(cbBtn(CallbackData.BUG_LABELS.get(CallbackData.BUG_PROXY), CallbackData.BUG_PROXY)),
                        new InlineKeyboardRow(cbBtn(CallbackData.BUG_LABELS.get(CallbackData.BUG_TG),    CallbackData.BUG_TG)),
                        new InlineKeyboardRow(cbBtn(CallbackData.BUG_LABELS.get(CallbackData.BUG_OTHER), CallbackData.BUG_OTHER)),
                        new InlineKeyboardRow(cbBtn("🔙 Назад", CallbackData.BACK_MAIN))
                ))
                .build();
    }

    public static InlineKeyboardMarkup channelPost(String site, String channel, String bot, String donate) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        new InlineKeyboardRow(urlBtn("🚀 Полный список", site)),
                        new InlineKeyboardRow(urlBtn("📢 Канал", channel), urlBtn("🤖 Нира", bot)),
                        new InlineKeyboardRow(urlBtn("❤️ Поддержать проект", donate))
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
