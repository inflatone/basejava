package ru.javaops.basejava.webapp.storage;


import ru.javaops.basejava.webapp.storage.serializer.JsonStreamSerializer;

import static ru.javaops.basejava.webapp.storage.AbstractArrayStorageTest.STORAGE_DIR;

public class JsonFileStorageTest extends AbstractStorageTest {
    public JsonFileStorageTest() {
        super(new FileStorage(STORAGE_DIR, new JsonStreamSerializer()));
    }
}