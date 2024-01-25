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
@Feature("Публичные SSH ключи")
@Epic("Cloud Compute")
public class PublicSshKeysTest extends AbstractComputeTest {

    private final EntitySupplier<String> publicKey = lazy(() -> {
        String name = getRandomName();
        new IndexPage().goToSshKeys().addKey(name, "user");
        return name;
    });

    @Test
    @TmsLink("1249435")
    @Order(1)
    @DisplayName("Cloud Compute. Добавление SSH-ключа (публичный)")
    void addKey() {
        publicKey.run();
    }

    @Test
    @TmsLink("1398316")
    @Order(2)
    @DisplayName("Cloud Compute. Редактирование SSH-ключа (публичный)")
    void editKey() {
        String key = publicKey.get();
        String newName = getRandomName();
        new IndexPage().goToSshKeys().editKey(key, newName);
        publicKey.set(newName);
    }

    @Test
    @TmsLink("1398321")
    @Order(3)
    @DisplayName("Cloud Compute. Удаление SSH-ключа (публичный)")
    void deleteKey() {
        String key = publicKey.get();
        new IndexPage().goToSshKeys().deleteKey(key);
    }
}
