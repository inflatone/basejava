package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.Config;

public class SQLStorageTest extends AbstractStorageTest {
    public SQLStorageTest() {
        super(Config.get().getStorage());
    }
}