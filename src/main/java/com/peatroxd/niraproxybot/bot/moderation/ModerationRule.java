package com.peatroxd.niraproxybot.bot.moderation;

import com.peatroxd.niraproxybot.bot.moderation.decision.ModerationResult;

public interface ModerationRule {

    ModerationResult evaluate(ModerationContext context);
}
