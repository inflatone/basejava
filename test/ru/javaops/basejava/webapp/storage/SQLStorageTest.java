package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.Config;

public class SQLStorageTest extends AbstractStorageTest {
    public SQLStorageTest() {
        super(
                new SQLStorage(
                        Config.get().getDbUrl(),
                        Config.get().getDbUser(),
                        Config.get().getDbPassword())
        );
    }
}