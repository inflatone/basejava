package ru.javaops.basejava.webapp;

import ru.javaops.basejava.webapp.model.Resume;

import java.lang.reflect.Field;

/**
 * Reflection
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public class MainReflection {

    public static void main(String[] args) throws ReflectiveOperationException {
        Resume r = new Resume();
        Class<? extends Resume> clazz = r.getClass();
        Field field = clazz.getDeclaredFields()[0];
        field.setAccessible(true);
        System.out.println(field.getName());
        System.out.println(field.get(r));
        field.set(r, "new_uuid");
        System.out.println(clazz.getDeclaredMethod("toString").invoke(r));
        System.out.println(r);
    }
}
