package ru.javaops.basejava.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    static final File PROPS = new File("./config/resumes.properties");
    private static Config INSTANCE = new Config();
    private Properties props = new Properties();
    private String storageDir;

    private Config() {
        try (InputStream is = new FileInputStream(PROPS)) {
            props.load(is);
            storageDir = props.getProperty("storage.dir");
            System.out.println(storageDir);
        } catch (IOException e) {
            throw new IllegalStateException("Invalid config file " + PROPS.getAbsolutePath());
        }
    }

    public static Config get() {
        return INSTANCE;
    }

    public String getStorageDir() {
        return storageDir;
    }
}
