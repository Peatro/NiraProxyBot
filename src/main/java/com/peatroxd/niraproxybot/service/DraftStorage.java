package com.peatroxd.niraproxybot.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DraftStorage {

    private final Map<Long, String> drafts = new ConcurrentHashMap<>();

    public void save(Long userId, String text) {
        drafts.put(userId, text);
    }

    public String get(Long userId) {
        return drafts.get(userId);
    }

    public void clear(Long userId) {
        drafts.remove(userId);
    }
}
