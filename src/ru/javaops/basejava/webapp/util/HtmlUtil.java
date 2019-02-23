package ru.javaops.basejava.webapp.util;

import ru.javaops.basejava.webapp.model.Organization;

/**
 * Utility methods for dealing with mapping data on HTML
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-23
 */
public class HtmlUtil {
    public static String formatDates(Organization.Position position) {
        return DateUtil.format(position.getStartDate()) + " - " + DateUtil.format(position.getEndDate());
    }

    public static boolean isEmpty(String line) {
        return line == null || line.trim().length() == 0;
    }
}
