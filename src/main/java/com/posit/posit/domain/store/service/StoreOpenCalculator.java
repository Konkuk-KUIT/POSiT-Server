package com.posit.posit.domain.store.service;

import com.posit.posit.domain.store.entity.Weekday;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * store.open_time(예: "09:00-18:00") + store.not_open(예: "MON") 기반으로
 * 영업 상태 코드를 계산한다.
 * 반환 규칙:
 * - HOLIDAY: 오늘 요일 == notOpen
 * - OPEN: 현재 시간이 영업시간 범위 내
 * - CLOSED: 그 외
 * - UNKNOWN: 파싱 실패/값 이상
 */
public final class StoreOpenCalculator {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private StoreOpenCalculator() {}

    /**
     * @param openTime store.open_time ("HH:mm-HH:mm")
     * @param notOpen  store.not_open ("MON".."SUN") 또는 null
     */
    public static String calculateStatusCode(String openTime, Weekday notOpen) {
        try {
            // 1️⃣ 휴무일 체크
            if (notOpen != null) {
                DayOfWeek today = LocalDate.now(ZONE).getDayOfWeek();
                if (today == toDayOfWeek(notOpen)) {
                    return "HOLIDAY";
                }
            }

            // 2️⃣ 영업시간 파싱
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

    private static DayOfWeek toDayOfWeek(Weekday weekday) {
        return switch (weekday) {
            case MON -> DayOfWeek.MONDAY;
            case TUE -> DayOfWeek.TUESDAY;
            case WED -> DayOfWeek.WEDNESDAY;
            case THU -> DayOfWeek.THURSDAY;
            case FRI -> DayOfWeek.FRIDAY;
            case SAT -> DayOfWeek.SATURDAY;
            case SUN -> DayOfWeek.SUNDAY;
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
     * - 일반 케이스: start < end  (예: 09:00-18:00)
     * - 자정 넘어감: start > end  (예: 18:00-02:00)
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
