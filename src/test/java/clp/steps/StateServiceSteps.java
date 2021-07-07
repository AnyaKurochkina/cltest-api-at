package clp.steps;

import clp.core.exception.CustomException;
import clp.core.helpers.Configurier;
import cucumber.api.java.ru.Тогда;
import io.restassured.RestAssured;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.response.Response;

import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.List;


public class StateServiceSteps extends Specifications {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceSteps.class);
    private static final String folder_logs = Configurier.getInstance().getAppProp("folder.logs");

    @Тогда("^Получить логи об ошибке из Оркестратора$")
    public static void GetErrorFromOrch(String order_id) throws JsonPathException, CustomException {
        List<String> traceback = null;
        Response resp = RestAssured
                .given()
                .spec(getRequestSpecificationSS())
                .queryParam("order_id", order_id)
                .when()
                .get("actions");
        try {
            traceback = resp.jsonPath().get("list");
        } catch (JsonPathException e) {
            log.error(e.getMessage());
        }
        log.error(String.valueOf(traceback));
        try {
            // Возьмите файл
            File file = new File(folder_logs + "/" + order_id + ".txt");
            //Создайте новый файл
            // Убедитесь, что он не существует
            if (file.createNewFile())
                System.out.println("File created");
            else
                System.out.println("File already exists");

            FileWriter writer = new FileWriter(file);
            writer.write(String.valueOf(traceback));
            writer.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        throw new CustomException("Error with VM");
    }

}
