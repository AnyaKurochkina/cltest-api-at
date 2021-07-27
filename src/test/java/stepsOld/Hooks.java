package stepsOld;

import core.helper.Configurier;
import core.helper.ShareData;
import core.vars.LocalThead;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.FileOutputStream;
import java.util.Properties;

@Log4j2
public class Hooks extends Steps {

    private ShareData shareData = new ShareData();

    @BeforeEach
    public void beforeScenarios() throws NoSuchFieldException, IllegalAccessException {
        AuthSteps authSteps = new AuthSteps();
        LocalThead.setTestVars(testVars);
        authSteps.getToken();
        shareData.load("/shareFolder/dataJson.json");
    }

    @AfterEach
    public void afterScenarios(){
        shareData.save("/shareFolder/dataJson.json");
        LocalThead.setTestVars(null);
    }

    @AfterAll
    static void afterAll(){
        createAllurePropertyFile();
    }

    //Этот метод нужен для отображения ENVIRONMENT в отчете allure
    public static void createAllurePropertyFile () {
        String path = "target/allure-results";
        try {
            Properties props = new Properties();
            props.setProperty("Environment", System.getProperty("env"));
            FileOutputStream fos = new FileOutputStream(path + "/environment.properties");
            props.store(fos, "");
            fos.close();
        } catch (Exception ex) {
            log.error("IO problem");
            ex.printStackTrace();
        }
    }


    private static final String URL = Configurier.getInstance().getAppProp("host_kk");
}
