package com.peatroxd.niraproxybot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public record ChannelPost(String text, InlineKeyboardMarkup keyboard) {
}
