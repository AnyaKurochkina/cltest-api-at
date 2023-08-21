package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.tests.engine.AbstractComputeTest;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Личные SSH ключи")
@Epic("Cloud Compute")
public class PrivateSshKeysTest extends AbstractComputeTest {
    String name = getRandomName();

    @Test
    @TmsLink("1194015")
    @Order(1)
    @DisplayName("Cloud Compute. Добавление SSH-ключа (личный)")
    void addKey() {
        new IndexPage().goToProfile().getSshKeys().addKey(name, "user");
    }

    @Test
    @TmsLink("1398313")
    @Order(2)
    @DisplayName("Cloud Compute. Удаление SSH-ключа (личный)")
    void deleteKey() {
        new IndexPage().goToProfile().getSshKeys().deleteKey(name);
    }
}
