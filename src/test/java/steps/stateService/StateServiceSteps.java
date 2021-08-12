package steps.stateService;

import core.exception.CustomException;
import core.helper.Configure;
import core.helper.Http;
import io.restassured.path.json.exception.JsonPathException;
import lombok.extern.log4j.Log4j2;
import steps.Steps;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

@Log4j2
public class StateServiceSteps extends Steps {
    private static final String URL = Configure.getInstance().getAppProp("host_ss");

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
