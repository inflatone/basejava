package ru.javaops.basejava.webapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.javaops.basejava.webapp.model.Section;

import java.io.Reader;
import java.io.Writer;

/**
 * Utility class for (de)serialization resumes to/from json files
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public class JsonParser {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Section.class, new JsonSectionAdapter<>())
            .create();

    public static <T> T read(Reader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }

    public static <T> void write(T instance, Writer writer) {
        GSON.toJson(instance, writer);
    }

    public static <T> T read(String content, Class<T> clazz) {
        return GSON.fromJson(content, clazz);
    }

    public static <T> String write (T instance, Class<T> clazz) {
        return GSON.toJson(instance, clazz);
    }
}