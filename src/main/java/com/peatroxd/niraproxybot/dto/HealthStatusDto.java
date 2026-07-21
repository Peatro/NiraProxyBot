package com.peatroxd.niraproxybot.dto;

public record HealthStatusDto(
        boolean verificationHealthy,
        String lastCheckedAt,
        Long ageSeconds,
        String egressCountry
) {}
