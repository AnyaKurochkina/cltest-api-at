package steps;

import core.exception.CustomException;
import core.helper.Configurier;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

@Log4j2
public class StateServiceSteps extends Steps {

    private static final String folder_logs = Configurier.getInstance().getAppProp("folder.logs");
    private static final String URL = Configurier.getInstance().getAppProp("host_ss");

    @Step("Получить логи об ошибке из Оркестратора {order_id}")
    public void GetErrorFromOrch(String order_id) throws JsonPathException, CustomException {
        List<String> traceback = null;
        try {
            traceback = new Http(URL)
                    .get("actions/?order_id=" + order_id)
                    .jsonPath().get("list.findAll{it.status.contains('error')}.data.traceback");
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
