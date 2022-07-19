package steps.day2;

import core.helper.Configure;
import core.helper.http.Http;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import steps.Steps;

@Log4j2
public class Day2Steps extends Steps {

    //TODO: пока так. когда дойдет апи тестов на весь сервис переделать на модели

    public static JsonPath getOperationsGraph(String id, String projectId) {
        return new Http(Configure.Day2ServiceURL)
                .get("/api/v1/projects/{}/operations/{}?include=graph_actions", projectId, id)
                .jsonPath();
    }

    public static String getOperations(String operationCardId, String projectId) {
        return new Http(Configure.Day2ServiceURL)
                .get("/api/v1/projects/{}/operations?include=total_count&page=1&per_page=10&operation_card_id={}", projectId, operationCardId)
                .jsonPath()
                .getString("list[0].id");
    }
}
