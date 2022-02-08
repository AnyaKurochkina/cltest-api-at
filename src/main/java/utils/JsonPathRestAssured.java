package utils;

import io.restassured.path.json.JsonPath;

public class JsonPathRestAssured {

    public static void main(String[] args) {
        JsonPath jsonPath = new JsonPath(json);
        Object o = jsonPath.get("data.status");
        System.out.println(o);
    }

    static final String json = "{\"data\": [{\n" +
            "    \"service_account\": \"sa_proj-4o6l1ld87k-bs\",\n" +
            "    \"updated_at\": \"2022-02-08T19:40:53Z\",\n" +
            "    \"action_id\": \"e98aa6c4-8b88-4b2f-81bb-1fa41a12b46a\",\n" +
            "    \"name\": \"sa_proj-4o6l1ld87k-bs\",\n" +
            "    \"description\": \"Ключ\",\n" +
            "    \"access_id\": \"c2FfcHJvai00bzZsMWxkODdrLWJz\",\n" +
            "    \"hcp_storage_user_guid\": \"fc7d9e51-5f86-48dd-bb95-bfd99b63cf04\",\n" +
            "    \"created_at\": \"2022-02-08T19:40:49Z\",\n" +
            "    \"order_id\": \"0898a797-ee0f-4cce-8b66-568aa9f40077\",\n" +
            "    \"status\": \"deleting\"\n" +
            "}]}";
}
