package tests;

import com.codeborne.selenide.Condition;
import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Attachment;
import lombok.SneakyThrows;
import org.junit.CustomDisplayNameGenerator;
import org.junit.EnvironmentCondition;
import org.junit.TmsLinkExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ru.testit.junit5.JUnit5EventListener;
import ru.testit.utils.UniqueTest;

import java.util.Collections;

import static io.qameta.allure.Allure.getLifecycle;

@ExtendWith(TmsLinkExtension.class)
@ExtendWith(EnvironmentCondition.class)
@ExtendWith(JUnit5EventListener.class)
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class Tests {
    public final static Condition activeCnd = Condition.and("visible and enabled", Condition.visible, Condition.enabled);
    public final static Condition clickableCnd = Condition.not(Condition.cssValue("cursor", "default"));

    @BeforeEach
    @SneakyThrows
    @Title("Инициализация логирования")
    public void beforeScenarios(TestInfo testInfo){
//        String className = testInfo.getTestClass().orElseThrow(Exception::new).getSimpleName();
//        String methodName = testInfo.getTestMethod().orElseThrow(Exception::new).getName();
//        Allure.tms(className + "#" + methodName, "");
        UniqueTest.clearStepLog();
    }

    public static void putAttachLog(String text) {
        UniqueTest.writeStepLog(text);
//        String stepId = getLifecycle().getCurrentTestCase().orElse(null);
//        if (stepId == null)
//            return;
//        String source = stepId + "-attachment.txt";
//        DataFileHelper.appendToFile(Configure.getAppProp("allure.results") + source, text + "\n");
//        Attachment attachment = new Attachment().setSource(source).setName("log-test.log");
//        getLifecycle().updateTestCase(stepId, s -> s.setAttachments(Collections.singletonList(attachment)));
    }

//    public static String getAttachLog() {
//        return UniqueTest.getStepLog();
//    }

//    public static boolean isAttachLog() {
//        return UniqueTest.getStepLog() != null;
//    }

}
