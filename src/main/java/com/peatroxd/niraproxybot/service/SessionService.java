package com.peatroxd.niraproxybot.service;

import com.peatroxd.niraproxybot.model.UserSession;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    public UserSession getSession(Long userId) {
        return sessions.computeIfAbsent(userId, id -> new UserSession());
    }

    public void reset(Long userId) {
        sessions.put(userId, new UserSession());
    }
}
