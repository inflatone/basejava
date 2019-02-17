package ru.javaops.basejava.webapp.model;

import java.util.Objects;

/**
 * Single line resume section
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-16
 */
public class TextSection extends Section {
    private static final long serialVersionUID = 1L;

    private final String content;

    public TextSection(String content) {
        Objects.requireNonNull(content, "content must not be null");
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextSection that = (TextSection) o;
        return content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return content;
    }
}
