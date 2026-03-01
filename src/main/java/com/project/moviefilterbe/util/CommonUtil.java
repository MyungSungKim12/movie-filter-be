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

    public static String getConversionPlatform(String platform) {
        String result;
        if(platform.indexOf("Disney") != -1) {
            result = "DISNEY";
        } else if(platform.indexOf("Netflix") != -1) {
            result = "NETFLIX";
        } else if(platform.indexOf("wavve") != -1) {
            result = "WAVVE";
        } else if(platform.indexOf("Watcha") != -1) {
            result = "WATCHA";
        } else if(platform.indexOf("Amazon") != -1) {
            result = "AMAZON";
        } else if(platform.indexOf("Coupang") != -1) {
            result = "COUPANG";
        } else {
            result = platform;
        }
        return result;
    }
}
