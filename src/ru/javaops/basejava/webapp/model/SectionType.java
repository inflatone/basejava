package ru.javaops.basejava.webapp.model;

/**
 * Section types of resume object
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-16
 */
public enum SectionType {
    OBJECTIVE("Позиция"),
    PERSONAL("Личные качества"),
    ACHIEVEMENT("Достижения"),
    QUALIFICATIONS("Квалификация"),
    EXPERIENCE("Опыт работы"),
    EDUCATION("Образование");

    private String title;

    SectionType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}