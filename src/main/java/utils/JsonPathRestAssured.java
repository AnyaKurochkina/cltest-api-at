package utils;

import io.restassured.path.json.JsonPath;

public class JsonPathRestAssured {

    public static void main(String[] args) {
        JsonPath jsonPath = new JsonPath(json);
        Object o = jsonPath.get("list.find{it.title=='vm'}.id");
        System.out.println(o);
    }

    static final String json = "[{\"id\":\"76266cb5-7d7d-4b23-a4cb-267b5db9d49a\",\"name\":\"tpl_linux_rhel_7.9_x86_64_en\",\"data\":{\"os\":{\"type\":\"linux\",\"vendor\":\"ibm\",\"version\":\"7.9\",\"architecture\":\"x86_64\",\"distribution\":\"rhel\",\"localization\":\"en\"},\"size\":30},\"directory\":\"f3ff5ac5-43c4-4e49-83b5-54d61db527ae\"},{\"id\":\"4c2002cf-5d94-4f12-8e12-175e944b9c32\",\"name\":\"tpl_linux_rhel_8.4_x86_64_en\",\"data\":{\"os\":{\"type\":\"linux\",\"vendor\":\"ibm\",\"version\":\"8.4\",\"architecture\":\"x86_64\",\"distribution\":\"rhel\",\"localization\":\"en\"},\"size\":30},\"directory\":\"f3ff5ac5-43c4-4e49-83b5-54d61db527ae\"}]";
}
