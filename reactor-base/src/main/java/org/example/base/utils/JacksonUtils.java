package org.example.base.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class JacksonUtils {

    private static final ObjectMapper OBJECT_MAPPER = getRegularObjectMap();

    public static ObjectMapper getObjectMapper() {

        return OBJECT_MAPPER;

    }

    public static <T> String toJson(T t) {
        try {
            if (t instanceof String) {
                return (String) t;
            }
            return getObjectMapper().writeValueAsString(t);
        } catch (Exception e) {
            log.warn("解析成json出错", e);
        }
        return null;
    }

    public static <T> JsonNode toJsonNode(T t) {
        try {
            String json = toJson(t);
            return getObjectMapper().readTree(json);
        } catch (Exception e) {
            log.warn("解析成json出错", e);
        }
        return null;
    }

    public static JsonNode toJsonNode(String jsonStr) {
        try {
            return getObjectMapper().readTree(jsonStr);
        } catch (Exception e) {
            log.warn("解析成json出错", e);
        }
        return null;
    }

    public static <T> T toObj(String json, Class<T> tClass) {
        T t = null;
        try {
            t = getObjectMapper().readValue(json, tClass);
        } catch (JsonProcessingException e) {
            log.warn("解析成json出错", e);
        }
        return t;
    }

    public static Object toObject(String json, JavaType javaType) throws Exception {
        return getObjectMapper().readValue(json, javaType);
    }

    public static <T> List<T> toList(String json, Class<T> tClass) {
        try {
            JavaType javaType = getCollectionType(List.class, tClass);
            return getObjectMapper().readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> toList(String json) {
        try {
            return getObjectMapper().readValue(json, new TypeReference<List<T>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Set<T> toSet(String json, Class<T> tClass) {
        List<T> list = toList(json, tClass);
        return CollectionUtils.isEmpty(list) ? new HashSet<>() : new HashSet<>(list);
    }


    public static <K, V> Map<K, V> toMap(String json) {
        try {
            return getObjectMapper().readValue(json, new TypeReference<Map<K, V>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isJson(String json) {
        boolean result = false;
        try {
            OBJECT_MAPPER.readTree(json);
            result = true;
        } catch (JsonProcessingException e) {
            log.warn("解析成json出错", e);
        }
        return result;
    }


    public static TypeFactory getTypeFactory() {
        return getObjectMapper().getTypeFactory();
    }

    public static String simplify(String jsonStr) {
        String str = jsonStr;
        if (isJson(jsonStr)) {
            try {
                JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonStr);
                str = OBJECT_MAPPER.writeValueAsString(jsonNode);
            } catch (Exception e) {
                log.warn("解析成json出错", e);
            }
        }
        return str;
    }

    public static <T> String simple(T t) {
        String result = null;
        try {
            if (t instanceof String) {
                result = simplify((String) t);
            } else {
                result = OBJECT_MAPPER.writeValueAsString(t);
            }
        } catch (Exception e) {
            log.warn("解析成json出错", e);
        }
        return result;
    }

    public static <T> T readJson(String json, String jsonPointer, Class<T> tClass) {
        JsonNode jsonNode = toJsonNode(json);
        JsonNode findNode = jsonNode.at(jsonPointer);
        try {
            return getObjectMapper().readValue(findNode.toString(), tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> readJson2List(String json, String jsonPointer, Class<T> tClass) {
        JsonNode jsonNode = toJsonNode(json);
        JsonNode findNode = jsonNode.at(jsonPointer);
        JavaType javaType = getCollectionType(List.class, tClass);
        try {
            return getObjectMapper().readValue(findNode.toString(), javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static ObjectMapper getRegularObjectMap() {
        ObjectMapper om = new ObjectMapper();
        om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        om.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.findAndRegisterModules();
        SimpleModule module = new SimpleModule();
        om.registerModule(module);
        return om;
    }

}
