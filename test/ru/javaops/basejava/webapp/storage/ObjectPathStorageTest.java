package ru.javaops.basejava.webapp.storage;


import ru.javaops.basejava.webapp.storage.serializer.ObjectStreamSerializer;

import static ru.javaops.basejava.webapp.storage.AbstractArrayStorageTest.STORAGE_DIR;

public class ObjectPathStorageTest extends AbstractStorageTest {
    public ObjectPathStorageTest() {
        super(new PathStorage(STORAGE_DIR, new ObjectStreamSerializer()));
    }
}