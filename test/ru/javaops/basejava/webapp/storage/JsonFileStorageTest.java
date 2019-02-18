package ru.javaops.basejava.webapp.storage;


import ru.javaops.basejava.webapp.storage.serializer.JsonStreamSerializer;

public class JsonFileStorageTest extends AbstractStorageTest {
    public JsonFileStorageTest() {
        super(new FileStorage(STORAGE_DIR, new JsonStreamSerializer()));
    }
}