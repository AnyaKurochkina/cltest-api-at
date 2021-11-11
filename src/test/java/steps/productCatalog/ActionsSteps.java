package steps.productCatalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.CacheService;
import core.helper.Http;
import io.restassured.response.ValidatableResponse;
import lombok.SneakyThrows;
import models.productCatalog.StringModel.StringModelResponse;
import models.productCatalog.testModel.ActionResponse;
import models.tarifficator.TariffPlan;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class ActionsSteps {

    @SneakyThrows
    @Test
    public void test() {
//        String object = new Http("http://d4-product-catalog.apps.d0-oscp.corp.dev.vtb/")
//                .setContentType("application/json")
//                .setWithoutToken()
//                .get("actions")
//                .assertStatus(200)
//                .toString();

        String object = "{\n" +
                "  \"service_account\": {\n" +
                "    \"title\": \"TEST02\",\n" +
                "    \"policy\": {\n" +
                "      \"bindings\": [\n" +
                "        {\n" +
                "          \"role\": null\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        StringModelResponse response = mapResponseOnClass(object, StringModelResponse.class);
        StringModelResponse responseGson = CacheService.getCustomGson().fromJson(object, StringModelResponse.class);


        System.out.println();


    }

    @SneakyThrows
    public static <T> T mapResponseOnClass(String rawJson, Class<T> clazz){
        JSONObject jsonObject = new JSONObject(rawJson);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(jsonObject.toMap(), clazz);
    }
}

//class MyTypeAdapter extends TypeAdapter<TestObject>(){
//
//@Override
//public void write(JsonWriter out,TestObject value)throws IOException{
//        out.beginObject();
//        if(!Strings.isNullOrEmpty(value.test1)){
//        out.name("test1");
//        out.value(value.test1);
//        }
//
//
//        if(!Strings.isNullOrEmpty(value.test2)){
//        out.name("test2");
//        out.value(value.test1);
//        }
//        /* similar check for otherObject */
//        out.endObject();
//        }
//
//@Override
//public TestObject read(JsonReader in)throws IOException{
//        // do something similar, but the other way around
//        }
//        }
//
