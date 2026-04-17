package com.peatroxd.niraproxybot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "telegram.bot.startup-enabled=false")
class NiraProxyBotApplicationTests {

    @Test
    void contextLoads() {
    }

}
