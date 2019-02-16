package ru.javaops.basejava.webapp.util;

import java.time.LocalDate;
import java.time.Month;

/**
 * Utility methods for dealing with dates
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-16
 */
public class DateUtil {
    public static LocalDate of(int year, Month month) {
        return LocalDate.of(year, month, 1);
    }
}
