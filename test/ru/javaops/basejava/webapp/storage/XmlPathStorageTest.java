package ru.javaops.basejava.webapp.storage;


import ru.javaops.basejava.webapp.storage.serializer.XmlStreamSerializer;

import static ru.javaops.basejava.webapp.storage.AbstractArrayStorageTest.STORAGE_DIR;

public class XmlPathStorageTest extends AbstractStorageTest {
    public XmlPathStorageTest() {
        super(new PathStorage(STORAGE_DIR, new XmlStreamSerializer()));
    }
}