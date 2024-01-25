package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Личные SSH ключи")
@Epic("Cloud Compute")
public class PrivateSshKeysTest extends AbstractComputeTest {

    private final EntitySupplier<String> privateKey = lazy(() -> {
        String name = getRandomName();
        new IndexPage().goToProfile().getSshKeys().addKey(name, "user");
        return name;
    });

    @Test
    @TmsLink("1194015")
    @Order(1)
    @DisplayName("Cloud Compute. Добавление SSH-ключа (личный)")
    void addKey() {
        privateKey.run();
    }

    @Test
    @TmsLink("1398313")
    @Order(2)
    @DisplayName("Cloud Compute. Удаление SSH-ключа (личный)")
    void deleteKey() {
        String key = privateKey.get();
        new IndexPage().goToProfile().getSshKeys().deleteKey(key);
    }
}
