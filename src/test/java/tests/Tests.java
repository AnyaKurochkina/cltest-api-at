package tests;

import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Allure;
import io.qameta.allure.aspects.StepsAspects;
import io.qameta.allure.model.Attachment;
import lombok.SneakyThrows;
import org.junit.CustomDisplayNameGenerator;
import org.junit.EnvironmentCondition;
import org.junit.TmsLinkExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.junit5.JUnit5EventListener;
import ru.testit.annotations.Title;
import ru.testit.utils.UniqueTest;

import java.text.SimpleDateFormat;
import java.util.*;

import static io.qameta.allure.Allure.getLifecycle;

@ExtendWith(TmsLinkExtension.class)
@ExtendWith(EnvironmentCondition.class)
@ExtendWith(JUnit5EventListener.class)
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class Tests {

    @BeforeEach
    @SneakyThrows
    @Title("Generate TMS link")
    public void beforeScenarios(TestInfo testInfo){
        String className = testInfo.getTestClass().orElseThrow(Exception::new).getSimpleName();
        String methodName = testInfo.getTestMethod().orElseThrow(Exception::new).getName();
        Allure.tms(className + "#" + methodName, "");
        UniqueTest.clearStepLog();
    }

    public static void putAttachLog(String text) {
        UniqueTest.writeStepLog(text);
        String stepId = getLifecycle().getCurrentTestCase().orElse(null);
        if (stepId == null)
            return;
        String source = stepId + "-attachment.txt";
        DataFileHelper.appendToFile(Configure.getAppProp("allure.results") + source, text + "\n");
        Attachment attachment = new Attachment().setSource(source).setName("log-test.log");
        getLifecycle().updateTestCase(stepId, s -> s.setAttachments(Collections.singletonList(attachment)));
    }

    public static String getAttachLog() {
        return UniqueTest.getStepLog();
    }

    public static boolean isAttachLog() {
        return UniqueTest.getStepLog() != null;
    }

    //    @AfterEach
//    public void afterScenarios(){
//    }
//    public void tmsLink(String id, String subId) {
//        Allure.tms(id + "." + subId, "");
//    }
}
