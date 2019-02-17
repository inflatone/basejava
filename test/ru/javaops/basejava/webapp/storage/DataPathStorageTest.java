package ru.javaops.basejava.webapp.storage;


import ru.javaops.basejava.webapp.storage.serializer.DataStreamSerializer;
import ru.javaops.basejava.webapp.storage.serializer.ObjectStreamSerializer;

import static ru.javaops.basejava.webapp.storage.AbstractArrayStorageTest.STORAGE_DIR;

public class DataPathStorageTest extends AbstractStorageTest {
    public DataPathStorageTest() {
        super(new PathStorage(STORAGE_DIR, new DataStreamSerializer()));
    }
}