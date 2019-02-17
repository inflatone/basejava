package ru.javaops.basejava.webapp.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

/**
 * Utility class to handle LocalDate serialization/deserialization
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(String date) throws Exception {
        return LocalDate.parse(date);
    }

    @Override
    public String marshal(LocalDate date) throws Exception {
        return date.toString();
    }
}
