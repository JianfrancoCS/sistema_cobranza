package edu.cibertec.taxihub.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class DateHelper {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("dd.MM.yyyy"),
        DateTimeFormatter.ISO_LOCAL_DATE
    );

    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        String trimmedDate = dateString.trim();

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmedDate, formatter);
            } catch (DateTimeParseException e) {
            }
        }

        throw new IllegalArgumentException("Unable to parse date: " + dateString + ". Supported formats: dd/MM/yyyy, dd-MM-yyyy, yyyy-MM-dd, yyyy/MM/dd, MM/dd/yyyy, dd.MM.yyyy, ISO format");
    }

    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }

        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatDateToStandard(LocalDate date) {
        return formatDate(date, "yyyy-MM-dd");
    }
}