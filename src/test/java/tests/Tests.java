package tests;

import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Attachment;
import lombok.SneakyThrows;
import org.junit.CustomDisplayNameGenerator;
import org.junit.EnvironmentCondition;
import org.junit.TmsLinkExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.junit5.JUnit5EventListener;
import ru.testit.annotations.Title;
import ru.testit.junit5.JUnit5EventListener;

import java.text.SimpleDateFormat;
import java.util.*;

import static io.qameta.allure.Allure.getLifecycle;

@ExtendWith(TmsLinkExtension.class)
@ExtendWith(EnvironmentCondition.class)
@ExtendWith(JUnit5EventListener.class)
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class Tests {
//    private static final ThreadLocal<StringBuilder> testLog = new ThreadLocal<>();
//    private static final ThreadLocal<Map<String, StringBuilder>> testLogMap = new ThreadLocal<>();

    @BeforeEach
    @SneakyThrows
    @Title("beforeScenarios")
    public void beforeScenarios(TestInfo testInfo){
        String className = testInfo.getTestClass().orElseThrow(Exception::new).getSimpleName();
        String methodName = testInfo.getTestMethod().orElseThrow(Exception::new).getName();
        Allure.tms(className + "." + methodName, "");
    }

    public static void putAttachLog(String text) {
        String stepId = getLifecycle().getCurrentTestCaseOrStep().orElse(null);
        if (stepId == null)
            return;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss ");
        String source = stepId + "-attachment.txt";
        DataFileHelper.appendToFile(Configure.getAppProp("allure.results") + source,
                formatter.format(new Date(System.currentTimeMillis())) + text);
        Attachment attachment = new Attachment().setSource(source).setName("log-step.log");
        getLifecycle().updateStep(stepId, s -> s.setAttachments(Collections.singletonList(attachment)));
    }

    //    @AfterEach
//    public void afterScenarios(){
//    }
//    public void tmsLink(String id, String subId) {
//        Allure.tms(id + "." + subId, "");
//    }
}
