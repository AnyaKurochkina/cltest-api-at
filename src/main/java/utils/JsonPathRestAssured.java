package utils;

import io.restassured.path.json.JsonPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonPathRestAssured {

    public static void main(String[] args) throws IOException {
        json = Files.lines(Paths.get("C:\\Users\\eXploit\\Downloads\\log-test - 2022-12-14T203619.812.log")).reduce("", String::concat);
        JsonPath jsonPath = new JsonPath(json);
//        Object o = jsonPath.get("data.find{it.type=='cluster'}.data.config.db_admin_group.any{it.dbms_role=='admin' && it.user_name.contains('cloud-zorg-dddd')}");
//           Object o = jsonPath.get(".find{it.name == 'kafka:zookeeper'}.id");
        System.out.println();
        //Object o = jsonPath.get("clients.list_user_type_name.any{it.name=='dfgdfg1'}");
        //Object o = jsonPath.get("clients.list_user_type_relationship.find{it.name==('dfgdfg1')}.relationship.any{it.name=='002'}");
        //.any{it.db_name=='%s'}
    }

    static String json = "";
}
