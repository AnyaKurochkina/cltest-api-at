package utils;

import io.restassured.path.json.JsonPath;

public class JsonPathRestAssured {

    public static void main(String[] args) {
        JsonPath jsonPath = new JsonPath(json);
//        Object o = jsonPath.get("data.find{it.data.config.containsKey('db_users')}.data.config.db_users.any{it.user_name=='%s'}");
           Object o = jsonPath.get(".find{it.name == 'kafka:zookeeper'}.id");
        System.out.println(o);
        //Object o = jsonPath.get("clients.list_user_type_name.any{it.name=='dfgdfg1'}");
        //Object o = jsonPath.get("clients.list_user_type_relationship.find{it.name==('dfgdfg1')}.relationship.any{it.name=='002'}");
        //.any{it.db_name=='%s'}
    }

    static final String json = "[\n" +
            "    {\n" +
            "        \"id\": \"7423d255-dd43-41f0-9713-092b5a417057\",\n" +
            "        \"name\": \"kafka:zookeeper\",\n" +
            "        \"data\": {\n" +
            "            \"kafka\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 1,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"zookeeper\": [\n" +
            "                \n" +
            "            ]\n" +
            "        },\n" +
            "        \"directory\": \"625764bf-2dc5-44c1-b50b-c41eb297c71a\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"db68395c-40b6-4b20-b29b-61671b4fa634\",\n" +
            "        \"name\": \"kafka-3:zookeeper-1\",\n" +
            "        \"data\": {\n" +
            "            \"kafka\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 3,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"zookeeper\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 1,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"directory\": \"625764bf-2dc5-44c1-b50b-c41eb297c71a\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"5dcb9900-1dc5-4c1d-89ed-55fec6964052\",\n" +
            "        \"name\": \"kafka-3:zookeeper-3\",\n" +
            "        \"data\": {\n" +
            "            \"kafka\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 3,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"zookeeper\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 3,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"directory\": \"625764bf-2dc5-44c1-b50b-c41eb297c71a\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"5fb8350d-d84e-4946-912c-c2a050a5cbd3\",\n" +
            "        \"name\": \"kafka-4:zookeeper-3\",\n" +
            "        \"data\": {\n" +
            "            \"kafka\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 4,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"zookeeper\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 3,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"directory\": \"625764bf-2dc5-44c1-b50b-c41eb297c71a\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"2022b202-f7d9-4616-9a24-aa9bc8c861f5\",\n" +
            "        \"name\": \"kafka-6:zookeeper-3\",\n" +
            "        \"data\": {\n" +
            "            \"kafka\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 6,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"zookeeper\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 3,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"directory\": \"625764bf-2dc5-44c1-b50b-c41eb297c71a\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"id\": \"d4c1b607-96a7-4954-8c47-ca855e505e78\",\n" +
            "        \"name\": \"kafka-8:zookeeper-3\",\n" +
            "        \"data\": {\n" +
            "            \"kafka\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 8,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"zookeeper\": [\n" +
            "                {\n" +
            "                    \"dc\": \"5\",\n" +
            "                    \"category\": \"vm\",\n" +
            "                    \"quantity\": 3,\n" +
            "                    \"data_center\": {\n" +
            "                        \"code\": \"5\",\n" +
            "                        \"name\": \"K37\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"directory\": \"625764bf-2dc5-44c1-b50b-c41eb297c71a\"\n" +
            "    }\n" +
            "]";
}
