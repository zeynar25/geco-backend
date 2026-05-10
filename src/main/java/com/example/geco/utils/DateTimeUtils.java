package com.example.geco.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtils {
    private static final DateTimeFormatter OUTPUT_DATE =
            DateTimeFormatter.ofPattern("MMMM d, uuuu", Locale.ENGLISH);

    private static final DateTimeFormatter OUTPUT_TIME =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);


    public static String formatDate(LocalDate date) {
        if (date == null) return null;
        return date.format(OUTPUT_DATE);
    }

    public static String formatTime(LocalTime time) {
        if (time == null) return null;
        return time.format(OUTPUT_TIME).toLowerCase(Locale.ENGLISH);
    }
}