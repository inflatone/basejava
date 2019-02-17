package ru.javaops.basejava.webapp.storage.serializer;

import ru.javaops.basejava.webapp.model.*;
import ru.javaops.basejava.webapp.util.XmlParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Xml stream serialization strategy
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public class XmlStreamSerializer implements StreamSerializer {
    private XmlParser xmlParser;

    public XmlStreamSerializer() {
        xmlParser = new XmlParser(
                Resume.class, Organization.class, Link.class, Organization.Position.class,
                OrganizationSection.class, TextSection.class, ListSection.class
        );
    }

    @Override
    public Void doWrite(Resume r, OutputStream os) throws IOException {
        try (Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            xmlParser.marshall(r, writer);
        }
        return null;
    }

    @Override
    public Resume doRead(InputStream is) throws IOException {
        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return xmlParser.unmarshall(reader);
        }
    }
}
