package com.peatroxd.niraproxybot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminAccessService {

    private final Set<Long> allowedIds;

    public AdminAccessService(@Value("${telegram.bot.allowed-admin-ids}") String raw) {
        this.allowedIds = Arrays.stream(raw.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    public boolean isAllowed(Long userId) {
        return allowedIds.contains(userId);
    }
}
