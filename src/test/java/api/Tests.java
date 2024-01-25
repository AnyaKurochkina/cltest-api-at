package api;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import core.helper.Configure;
import core.utils.Waiting;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import models.AbstractEntity;
import org.junit.CustomDisplayNameGenerator;
import org.junit.EnvironmentCondition;
import org.junit.TmsLinkExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ru.testit.junit5.JUnit5EventListener;
import ru.testit.utils.UniqueTest;
import ui.t1.pages.IndexPage;
import ui.t1.tests.engine.EntitySupplier;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

@ExtendWith(TmsLinkExtension.class)
@ExtendWith(EnvironmentCondition.class)
@ExtendWith(JUnit5EventListener.class)
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class Tests {
    public final static Condition activeCnd = Condition.and("visible and enabled", Condition.visible, Condition.enabled)
            .because("Элемент не активный");
    public final static Condition clickableCnd = Condition.not(Condition.cssValue("cursor", "default"))
            .because("Элемент не кликабельный");

    public static Runnable getPostLoadPage() {
        if (Configure.isT1())
            return () -> {
                if (!Waiting.sleep(() -> new IndexPage().getLinkProfile().isDisplayed(), Duration.ofSeconds(10))) {
                    Selenide.refresh();
                }
            };
        return () -> Waiting.sleep(() -> new IndexPage().getLinkProfile().isDisplayed(), Duration.ofSeconds(10));
    }

    @AfterAll
    static void afterAll() {
        AbstractEntity.deleteCurrentClassEntities();
    }

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

    @SneakyThrows
    protected static <T> EntitySupplier<T> lazy(Supplier<T> executable) {
        return new EntitySupplier<T>(executable);
    }
}
