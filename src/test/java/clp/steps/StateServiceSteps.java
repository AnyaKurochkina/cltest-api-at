package clp.steps;

import cucumber.api.java.ru.Тогда;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


public class StateServiceSteps extends Specifications {

    private static final Logger log = LoggerFactory.getLogger(OrderSteps.class);

    @Тогда("^Получить логи об ошибке из Оркестратора$")
    public static void GetErrorFromOrch(String order_id) throws ParseException {
        Response resp = RestAssured
                                .given()
                                .spec(getRequestSpecificationSS())
                                .queryParam("order_id", order_id)
                                .when()
                                .get("actions");
        System.out.println("resp = " + resp.getBody().asString());
        List <String> traceback = resp.jsonPath().get("list");
        System.out.println("traceback = " + traceback);
        }

}
