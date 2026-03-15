package com.project.moviefilterbe.util;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

public final class CommonUtil {

    private CommonUtil() {
        throw new AssertionError("Utility class");
    }

    public static String getGenerateId(String type) {
        return type + "_" + UUID.randomUUID().toString();
    }

    public static OffsetDateTime getDateTimeNow() {
        return OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public static OffsetDateTime getDateTimePlusDay(int days) {
        return getDateTimeNow().plusDays(days);
    }
}
