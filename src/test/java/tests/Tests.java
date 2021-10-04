package tests;


import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.junit.CustomDisplayNameGenerator;
import org.junit.TmsLinkExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;

import java.text.SimpleDateFormat;
import java.util.Date;

import static core.helper.JsonHelper.stringPrettyFormat;

@ExtendWith(TmsLinkExtension.class)
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class Tests {
    private static final ThreadLocal<StringBuilder> testLog = new ThreadLocal<>();

    @BeforeEach
    public void beforeScenarios(){
        testLog.remove();
        testLog.set(new StringBuilder());
    }

    @AfterEach
    public void afterScenarios(){
        attachment();
    }
    public void tmsLink(String id, String subId){
        Allure.tms(id + "." + subId, "");
    }

    private void attachment(){
        Allure.addAttachment("LOG", testLog.get().toString());
    }
    public static void putLog(String msg){
        if(testLog.get() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss ");
            testLog.get().append(formatter.format(new Date(System.currentTimeMillis()))).append(msg);
        }
    }

}
