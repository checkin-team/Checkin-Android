package com.checkin.app.checkin.Data;

import android.support.annotation.Nullable;
import android.util.Log;

import com.checkin.app.checkin.Notif.NotifModel;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.objectbox.converter.PropertyConverter;

public class Converters {
    private static final String TAG = Converters.class.getSimpleName();
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static class MapConverter<X, Y> implements PropertyConverter<Map<X, Y>, String> {

        @Override
        public Map<X, Y> convertToEntityProperty(String databaseValue) {
            TypeReference type = new TypeReference<Map<X, Y>>() {};
            return getObjectFromJson(databaseValue, type);
        }

        @Override
        public String convertToDatabaseValue(Map<X, Y> entityProperty) {
            try {
                return objectMapper.writeValueAsString(entityProperty);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    public static class ListConverter<T> implements PropertyConverter<List<T>, String> {

        @Override
        public List<T> convertToEntityProperty(String databaseValue) {
            TypeReference type = new TypeReference<List<T>>() {};
            return getObjectFromJson(databaseValue, type);
        }

        @Override
        public String convertToDatabaseValue(List<T> entityProperty) {
            try {
                return objectMapper.writeValueAsString(entityProperty);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    public static class ActionConverter implements PropertyConverter<NotifModel.ACTION, Integer> {
        @Override
        public NotifModel.ACTION convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            for (NotifModel.ACTION action : NotifModel.ACTION.values()) {
                if (action.id == databaseValue) {
                    return action;
                }
            }
            return NotifModel.ACTION.NULL;
        }

        @Override
        public Integer convertToDatabaseValue(NotifModel.ACTION entityProperty) {
            return entityProperty == null ? null : entityProperty.id;
        }
    }

    public static JsonNode getJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <T> T getObjectFromJson(String json, TypeReference typeReference) {
        T res;
        try {
            res = objectMapper.readValue(json, typeReference);
        } catch (IOException exception) {
            res = null;
            Log.e(TAG, "JSON invalid! " + json);
        }
        return res;
    }
}