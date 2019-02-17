package ru.javaops.basejava.webapp.storage.serializer;

import ru.javaops.basejava.webapp.model.Resume;
import ru.javaops.basejava.webapp.util.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Xml stream serialization strategy
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public class JsonStreamSerializer implements StreamSerializer {
    @Override
    public Void doWrite(Resume r, OutputStream os) throws IOException {
        try (Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            JsonParser.write(r, writer);
        }
        return null;
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return JsonParser.read(reader, Resume.class);
        }
    }
}