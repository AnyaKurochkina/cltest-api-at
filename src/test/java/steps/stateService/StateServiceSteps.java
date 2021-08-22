package steps.stateService;

import core.exception.CustomException;
import core.helper.Configure;
import core.helper.Http;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
import steps.Steps;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

@Log4j2
public class StateServiceSteps extends Steps {
    private static final String URL = Configure.getAppProp("host_ss");

    public String GetErrorFromOrch(IProduct product) throws JsonPathException, CustomException {
        List<String> traceback = null;
        try {
            traceback = new Http(URL)
                    .setProjectId(product.getProjectId())
                    .get("actions/?order_id=" + product.getOrderId())
                    .jsonPath().get("list.findAll{it.status.contains('error')}.data.traceback");
        } catch (JsonPathException e) {
            log.error(e.getMessage());
        }
//        log.error(String.valueOf(traceback));
        try {
            // Возьмите файл
            File file = new File(folder_logs + "/" + product.getOrderId() + ".txt");
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
            e.printStackTrace();
            log.error("Ошибка в GetErrorFromOrch " + e);
        }
        return String.valueOf(traceback);
    }
}
