package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.storage.serializer.ObjectStreamSerializer;

import static ru.javaops.basejava.webapp.storage.AbstractArrayStorageTest.STORAGE_DIR;

public class ObjectFileStorageTest extends AbstractStorageTest {
    public ObjectFileStorageTest() {
        super(new FileStorage(STORAGE_DIR, new ObjectStreamSerializer()));
    }
}
