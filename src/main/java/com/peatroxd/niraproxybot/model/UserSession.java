package com.peatroxd.niraproxybot.model;

import com.peatroxd.niraproxybot.enums.UserState;
import lombok.Data;

@Data
public class UserSession {
    private UserState state = UserState.IDLE;
    private String bugType;
}
