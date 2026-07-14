package com.peatroxd.niraproxybot.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DraftStorage {

    private final Map<Long, ChannelPost> drafts = new ConcurrentHashMap<>();

    public void save(Long userId, ChannelPost post) {
        drafts.put(userId, post);
    }

    public ChannelPost get(Long userId) {
        return drafts.get(userId);
    }

    public void clear(Long userId) {
        drafts.remove(userId);
    }
}
