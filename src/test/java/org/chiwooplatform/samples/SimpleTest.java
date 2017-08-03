package org.chiwooplatform.samples;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by seonbo.shim on 2017-07-06.
 */
@Slf4j
public class SimpleTest {

    static final int MINUTES_PER_HOUR = 60;

    static final int SECONDS_PER_MINUTE = 60;

    static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

    static Duration duration(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start.toLocalTime(), end.toLocalTime());
        return duration;
    }

    static Period elapsedPeriod(LocalDateTime start, LocalDateTime end) {
        Period period = Period.between(start.toLocalDate(), end.toLocalDate());
        return period;
    }

    static LocalDateTime utcToLocalDateTime(Date utcDate) {
        ZonedDateTime zonedDateTime = ZonedDateTime
                .ofInstant(utcDate.toInstant(), ZoneId.systemDefault())
                .withZoneSameLocal(ZoneId.of("UTC"));
        return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    static Date localDateTimeToUtc(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("UTC"))
                .withZoneSameLocal(ZoneId.systemDefault()).toInstant());
    }

    public static Date nowUTC() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.now(),
                ZoneOffset.UTC);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    static String elapsedHoursMinutesSeconds(LocalDateTime dateBegin,
            LocalDateTime dateFinish) {
        StringBuilder result = new StringBuilder();
        LocalDateTime tempDateTime = LocalDateTime.from(dateBegin);
        Long years = tempDateTime.until(dateFinish, ChronoUnit.YEARS);
        if (years > 0) {
            result.append(years + " years ");
        }
        tempDateTime = tempDateTime.plusYears(years);
        Long months = tempDateTime.until(dateFinish, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);
        if (months > 0) {
            result.append(months + " months ");
        }
        Long days = tempDateTime.until(dateFinish, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);
        if (days > 0) {
            result.append(days + " days ");
        }
        Long hours = tempDateTime.until(dateFinish, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);
        Long minutes = tempDateTime.until(dateFinish, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);
        Long seconds = tempDateTime.until(dateFinish, ChronoUnit.SECONDS);
        return result.append(String.format("%02d", hours) + ":"
                + String.format("%02d", minutes) + ":" + String.format("%02d", seconds))
                .toString();
    }

    static long getMinutesTime(LocalDateTime start, LocalDateTime end) {
        LocalDateTime today = LocalDateTime.of(start.getYear(), start.getMonthValue(),
                start.getDayOfMonth(), end.getHour(), end.getMinute(), end.getSecond());
        Duration duration = Duration.between(today, start);
        long seconds = duration.getSeconds();
        long hours = seconds / SECONDS_PER_HOUR;
        long minutes = ((seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
        long totalMinutes = hours * 60 + minutes;
        return totalMinutes;
    }

    @Test
    public void ut1001_localTime() {
        final LocalTime now = LocalTime.now();
        log.info("now: {}", now);
        final int hour = now.getHour();
        final int minute = now.getMinute();
        final int second = now.getSecond();
        log.info("hour: {}, minute: {}, second: {}", hour, minute, second);
    }

    @Test
    public void ut1002_localDate() {
        final LocalDate now = LocalDate.now();
        log.info("now: {}", now);
        final int year = now.getYear();
        final int month = now.getMonthValue();
        final int day = now.getDayOfMonth();
        log.info("hour: {}, minute: {}, second: {}", year, month, day);
    }

    @Test
    public void ut1003_localDateTime() {
        final LocalDateTime now = LocalDateTime.now();
        log.info("now: {}", now);
        final int year = now.getYear();
        final int month = now.getMonthValue();
        final int day = now.getDayOfMonth();
        final int hour = now.getHour();
        final int minute = now.getMinute();
        final int second = now.getSecond();
        log.info("year: {}, month: {}, day: {}", year, month, day);
        log.info("hour: {}, minute: {}, second: {}", hour, minute, second);
    }

    @Test
    public void ut1004_localDateTime() {
        final LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info("{} - now", now);
        LocalDateTime nowPlusHours = now.plusHours(1);
        log.info("{} - now + 1 hour", nowPlusHours);
    }

    @Test
    public void ut1005_durationTime() {
        final LocalDateTime from = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime to = from.plusMonths(2);
        Duration dur = duration(from, to);
        long mins = dur.getSeconds() / 60;
        log.info("{} - mins", mins);
    }

    @Test
    public void ut1006_periodTime() {
        final LocalDateTime from = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime to = from.plusDays(88);
        Period period = elapsedPeriod(from, to);
        final int year = period.getYears();
        final int month = period.getMonths();
        final int day = period.getDays();
        log.info("year: {}, month: {}, day: {}", year, month, day);
    }

    @Test
    public void ut1007_elapsedHoursMinutesSeconds() {
        final LocalDateTime from = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime to = from.plusHours(777);
        log.info("{}", elapsedHoursMinutesSeconds(from, to));
    }

    @Test
    public void ut1008_minutePeriod() {
        final LocalDateTime from = LocalDateTime.now(ZoneId.systemDefault());
        log.debug("{}", from);
        LocalDateTime to = from.plusHours(5).plusDays(1).plusMinutes(8);
        log.info("getMinutesTime: {}", ChronoUnit.MINUTES.between(from, to));
    }

    @Test
    public void ut1009_formattedString() {
        final LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        log.debug("{}", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        log.debug("{}",
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
    }
}
