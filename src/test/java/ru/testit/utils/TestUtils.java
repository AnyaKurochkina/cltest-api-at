package ru.testit.utils;

import core.helper.http.Http;
import core.utils.Waiting;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import ru.testit.services.TestITClient;

import java.util.List;

@Log4j2(topic = "LogTest")
public class TestUtils {

    @Test
    @SneakyThrows
    // Удаление всех автотестов
    void removeAllAutoTests() {
        List<Integer> response = new Http(TestITClient.properties.getUrl())
                .setSourceToken("PrivateToken " + TestITClient.properties.getPrivateToken())
                .get("/api/v2/autoTests?projectId={}", TestITClient.properties.getProjectID())
                .assertStatus(200)
                .jsonPath()
                .getList("globalId");

        for(Integer globalId : response){
            new Http(TestITClient.properties.getUrl())
                    .setSourceToken("PrivateToken " + TestITClient.properties.getPrivateToken())
                    .delete("/api/v2/autoTests/{}", globalId);
            Waiting.sleep(300);
        }
    }
}
