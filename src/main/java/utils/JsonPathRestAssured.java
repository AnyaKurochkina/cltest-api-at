package utils;

import io.restassured.path.json.JsonPath;

public class JsonPathRestAssured {

    public static void main(String[] args) {
        JsonPath jsonPath = new JsonPath(json);
//        Object o = jsonPath.get("data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}");
           Object o = jsonPath.get("clients.list_user_type_name.find{it.name==('dfgdfg1')}");
        System.out.println(o);
        //Object o = jsonPath.get("clients.list_user_type_name.any{it.name=='dfgdfg1'}");
        //Object o = jsonPath.get("clients.list_user_type_relationship.find{it.name==('dfgdfg1')}.relationship.any{it.name=='002'}");
        //.any{it.db_name=='%s'}
    }

    static final String json = "{\"list_services_and_clients_name\": [\n" +
            "            {\n" +
            "              \"name\": \"002\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"dfgdfg\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"dfgdfg1\"\n" +
            "            }\n" +
            "          ]}";
}
