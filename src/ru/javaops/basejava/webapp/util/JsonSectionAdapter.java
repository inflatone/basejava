package ru.javaops.basejava.webapp.util;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Utility class to handle Json (de)serialization of classes implementing {@link ru.javaops.basejava.webapp.model.Section}
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public class JsonSectionAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private static final String CLASSNAME = "CLASSNAME";
    private static final String INSTANCE = "INSTANCE";

    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();
        try {
            Class<?> clazz = Class.forName(className);
            return context.deserialize(jsonObject.get(INSTANCE), clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    @Override
    public JsonElement serialize(T source, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty(CLASSNAME, source.getClass().getName());
        JsonElement element = context.serialize(source);
        result.add(INSTANCE, element);
        return result;
    }
}