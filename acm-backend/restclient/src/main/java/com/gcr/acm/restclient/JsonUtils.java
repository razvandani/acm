package com.gcr.acm.restclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Razvan Dani.
 */
public class JsonUtils {
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static <T> T getEntity(String json, Object typeObject) {
        if (json == null) {
            return null;
        }

        if (typeObject instanceof JavaType) {
            return getEntity(json, (JavaType) typeObject);
        }

        if (typeObject instanceof TypeReference) {
            return getEntity(json, (TypeReference) typeObject);
        }

        if (typeObject instanceof Class) {
            return getEntity(json, (Class<T>) typeObject);
        }

        throw new IllegalArgumentException("Invalid type object");
    }

    public static <T> T getEntity(String json, JavaType javaType) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            log.error("Error deserializing json", e);
            return null;
        }
    }

    public static <T> T getEntity(String json, TypeReference typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("Error deserializing json", e);
            return null;
        }
    }

    public static <T> T getEntity(String json, Class<T> clazz) {
        try {
            if (String.class.equals(clazz)) {
                return (T) json;
            }
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("Error deserializing json", e);
            return null;
        }
    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error serializing json", e);
            return null;
        }
    }

    public static JavaType getCollectionType(Class<List> collectionClass, Class elementClass) {
        return JsonUtils.getObjectMapper().getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public static JavaType getMapType(Class<? extends Map> mapClass, Class keyClass, Class valueClass) {
        return JsonUtils.getObjectMapper().getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }
}
