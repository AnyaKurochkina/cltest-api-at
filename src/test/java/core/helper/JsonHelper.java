package core.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.EnumSet;
import java.util.Set;

import static steps.Steps.dataJson;

@Log4j2
@Data
public class JsonHelper {

    static {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JsonOrgJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    public static String toJson(Object e) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create().toJson(e, e.getClass());
    }

    public static ObjectMapper getCustomObjectMapper()  {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @SneakyThrows
    public static <T> T convertResponseOnClass(String rawJson, Class<T> clazz){
        JSONObject jsonObject = new JSONObject(rawJson);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(jsonObject.toMap(), clazz);
    }

    public static String getStringFromFile(String s) {
        try {
            File file = new File(dataJson + s);
            return FileUtils.readFileToString(file, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error(ex.getMessage());
        }
    }

    public static JSONObject getJsonFromFile(String file) {
        try {
            return new JSONObject(getStringFromFile(file));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error(ex.getMessage());
        }
    }

    public static String stringPrettyFormat(String text) {
        try {
            text = new JSONObject(text).toString(4);
        } catch (JSONException ex) {
            try {
                text = new JSONArray(text).toString(4);
            } catch (JSONException ex1) {
                return text;
            }
        }
        return text;
    }

    public static JsonTemplate getJsonTemplate(String file) {
        return new JsonTemplate(getJsonFromFile(file));
    }

}
