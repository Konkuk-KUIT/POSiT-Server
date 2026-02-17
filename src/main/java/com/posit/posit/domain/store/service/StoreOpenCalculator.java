package com.posit.posit.domain.store.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * store.open_time(예: "09:00-18:00") + store.not_open(예: "MON,TUE") 기반으로
 * 영업 상태 코드를 계산한다.
 */
public final class StoreOpenCalculator {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private StoreOpenCalculator() {}

    /**
     * @param openTime store.open_time ("HH:mm-HH:mm")
     * @param notOpen  store.not_open ("MON,TUE" 또는 null)
     */
    public static String calculateStatusCode(String openTime, String notOpen) { // 👈 파라미터 타입 변경 (Weekday -> String)
        try {
            // 1️⃣ 휴무일 체크 (다중 요일 지원)
            if (notOpen != null && !notOpen.isBlank()) {
                DayOfWeek today = LocalDate.now(ZONE).getDayOfWeek();

                // "MON,TUE" -> ["MON", "TUE"] 로 분리
                String[] holidays = notOpen.split(",");

                for (String h : holidays) {
                    // 공백 제거 후 비교 (혹시 모를 "MON, TUE" 대비)
                    if (toDayOfWeek(h.trim()) == today) {
                        return "HOLIDAY";
                    }
                }
            }

            // 2️⃣ 영업시간 파싱 (기존과 동일)
            TimeRange range = parseOpenTime(openTime);
            if (range == null) return "UNKNOWN";

            LocalTime now = LocalTime.now(ZONE);
            return isWithin(now, range.start, range.end)
                    ? "OPEN"
                    : "CLOSED";

        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    // 문자열(MON)을 자바 요일(MONDAY)로 변환
    private static DayOfWeek toDayOfWeek(String weekdayStr) {
        if (weekdayStr == null) return null;

        return switch (weekdayStr.toUpperCase()) {
            case "MON" -> DayOfWeek.MONDAY;
            case "TUE" -> DayOfWeek.TUESDAY;
            case "WED" -> DayOfWeek.WEDNESDAY;
            case "THU" -> DayOfWeek.THURSDAY;
            case "FRI" -> DayOfWeek.FRIDAY;
            case "SAT" -> DayOfWeek.SATURDAY;
            case "SUN" -> DayOfWeek.SUNDAY;
            default -> null; // 이상한 값이면 null 반환
        };
    }

    private static TimeRange parseOpenTime(String openTime) {
        if (openTime == null || openTime.isBlank()) return null;

        String normalized = openTime.replace(" ", "");
        String[] parts = normalized.split("-");
        if (parts.length != 2) return null;

        return new TimeRange(
                LocalTime.parse(parts[0]),
                LocalTime.parse(parts[1])
        );
    }

    /**
     * 영업시간 범위 판정.
     */
    private static boolean isWithin(LocalTime now, LocalTime start, LocalTime end) {
        if (start.equals(end)) return true; // 24시간 영업

        if (start.isBefore(end)) {
            return !now.isBefore(start) && now.isBefore(end);
        }

        // 자정 넘어가는 케이스 (18:00-02:00)
        return !now.isBefore(start) || now.isBefore(end);
    }

    private record TimeRange(LocalTime start, LocalTime end) {}
}