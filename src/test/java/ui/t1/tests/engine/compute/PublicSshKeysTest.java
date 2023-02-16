package ui.t1.tests.engine.compute;

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
@Feature("Публичные SSH ключи")
public class PublicSshKeysTest extends AbstractComputeTest {
    String name = getRandomName();

    @Test
    @TmsLink("1249435")
    @Order(1)
    @DisplayName("Cloud Compute. Добавление SSH-ключа (публичный)")
    void addKey() {
        new IndexPage().goToSshKeys().addKey(name, "user");
    }

    @Test
    @TmsLink("1398316")
    @Order(2)
    @DisplayName("Cloud Compute. Редактирование SSH-ключа (публичный)")
    void editKey() {
        String newName = getRandomName();
        new IndexPage().goToSshKeys().editKey(name, newName);
        name = newName;
    }

    @Test
    @TmsLink("1398321")
    @Order(3)
    @DisplayName("Cloud Compute. Удаление SSH-ключа (публичный)")
    void deleteKey() {
        new IndexPage().goToSshKeys().deleteKey(name);
    }
}
