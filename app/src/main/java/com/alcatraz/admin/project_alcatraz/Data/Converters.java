package com.alcatraz.admin.project_alcatraz.Data;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import io.objectbox.converter.PropertyConverter;

public class Converters {
    private static final String TAG = Converters.class.getSimpleName();
    public static class MapConverter<X, Y> implements PropertyConverter<Map<X, Y>, String> {

        @Override
        public Map<X, Y> convertToEntityProperty(String databaseValue) {
            Type type = new TypeToken<Map<X, Y>>() {}.getType();
            return getObjectFromJson(databaseValue, type);
        }

        @Override
        public String convertToDatabaseValue(Map<X, Y> entityProperty) {
            return new Gson().toJson(entityProperty);
        }
    }

    public static class ListConverter<T> implements PropertyConverter<List<T>, String> {

        @Override
        public List<T> convertToEntityProperty(String databaseValue) {
            Type type = new TypeToken<List<T>>() {
            }.getType();
            return getObjectFromJson(databaseValue, type);
        }

        @Override
        public String convertToDatabaseValue(List<T> entityProperty) {
            return new Gson().toJson(entityProperty);
        }
    }

    public static JsonObject getJsonObject(String json) {
        return getObjectFromJson(json, JsonObject.class);
    }

    @Nullable
    public static <T> T getObjectFromJson(String json, Type type) {
        T res;
        try {
            res = new Gson().fromJson(json, type);
        } catch (JsonSyntaxException | IllegalStateException exception) {
            res = null;
            Log.e(TAG, "JSON invalid! " + json);
        }
        return res;
    }
}