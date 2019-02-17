package ru.javaops.basejava.webapp.storage;

import org.junit.Assert;
import org.junit.Test;
import ru.javaops.basejava.webapp.exception.StorageException;
import ru.javaops.basejava.webapp.model.Resume;

import java.io.File;
import java.util.stream.IntStream;

public abstract class AbstractArrayStorageTest extends AbstractStorageTest {
    protected static final File STORAGE_DIR = new File("D:/projects/basejava/storage");

    public AbstractArrayStorageTest(Storage storage) {
        super(storage);
    }

    @Test(expected = StorageException.class)
    public void saveOverflow() {
        try {
            IntStream.range(3, AbstractArrayStorage.STORAGE_LIMIT).forEach(i -> storage.save(new Resume("Name" + i)));
        } catch (Exception e) {
            Assert.fail();
        }
        assertSize(AbstractArrayStorage.STORAGE_LIMIT);
        storage.save(new Resume("Overflow"));
    }

    public static void main(String[] args) {
        System.out.println(STORAGE_DIR.getAbsolutePath());
    }
}
