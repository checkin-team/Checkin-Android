package com.alcatraz.admin.project_alcatraz.Data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import io.objectbox.converter.PropertyConverter;

public class Converters {
    public static class MapConverter<X, Y> implements PropertyConverter<Map<X, Y>, String> {

        @Override
        public Map<X, Y> convertToEntityProperty(String databaseValue) {
            Type type = new TypeToken<Map<X, Y>>() {}.getType();
            return new Gson().fromJson(databaseValue, type);
        }

        @Override
        public String convertToDatabaseValue(Map<X, Y> entityProperty) {
            return new Gson().toJson(entityProperty);
        }
    }

    public static class ListConverter<T> implements PropertyConverter<List<T>, String> {

        @Override
        public List<T> convertToEntityProperty(String databaseValue) {
            Type type = new TypeToken<List<T>>() {}.getType();
            return new Gson().fromJson(databaseValue, type);
        }

        @Override
        public String convertToDatabaseValue(List<T> entityProperty) {
            return new Gson().toJson(entityProperty);
        }
    }
}