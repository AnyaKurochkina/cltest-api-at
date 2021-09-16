package tests;


import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.text.SimpleDateFormat;
import java.util.Date;

import static core.helper.JsonHelper.stringPrettyFormat;

public class Tests {
    private static final ThreadLocal<StringBuilder> testLog = new ThreadLocal<>();

    @BeforeEach
    public void beforeScenarios()  {
        testLog.remove();
        testLog.set(new StringBuilder());
    }

    @AfterEach
    public void afterScenarios(){
        attachment();
    }

    private void attachment(){
        Allure.addAttachment("LOG", testLog.get().toString());
    }
    public static void putLog(String msg){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss ");
        testLog.get().append(formatter.format(new Date(System.currentTimeMillis()))).append(msg);
    }

}
