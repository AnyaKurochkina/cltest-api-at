package api;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import models.AbstractEntity;
import org.junit.CustomDisplayNameGenerator;
import org.junit.EnvironmentCondition;
import org.junit.TmsLinkExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ru.testit.junit5.JUnit5EventListener;
import ru.testit.utils.UniqueTest;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
    public void beforeScenarios() {
        UniqueTest.clearStepLog();
    }

    @AfterEach
    @Title("Удаление сущностей")
    public void afterEach() {
        if (Objects.nonNull(UniqueTest.getStepLog()))
            Allure.getLifecycle().addAttachment("log-test", "text/html", "log", UniqueTest.getStepLog().getBytes(StandardCharsets.UTF_8));
        AbstractEntity.deleteCurrentTestEntities();
    }

    public static void putAttachLog(String text) {
        UniqueTest.writeStepLog(text);
    }

}
