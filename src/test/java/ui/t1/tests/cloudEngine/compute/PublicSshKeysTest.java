package ui.t1.tests.cloudEngine.compute;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.tests.cloudEngine.AbstractComputeTest;

@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
//    @TmsLink("")
    @Order(2)
    @DisplayName("Cloud Compute. Копирование SSH-ключа (публичный)")
    void copyKey() {
        new IndexPage().goToSshKeys().copyKey(name);
    }

    @Test
//    @TmsLink("")
    @Order(3)
    @DisplayName("Cloud Compute. Редактирование SSH-ключа (публичный)")
    void editKey() {
        String newName = getRandomName();
        new IndexPage().goToSshKeys().editKey(name, newName);
        name = newName;
    }

    @Test
//    @TmsLink("")
    @Order(4)
    @DisplayName("Cloud Compute. Удаление SSH-ключа (публичный)")
    void deleteKey() {
        new IndexPage().goToSshKeys().deleteKey(name);
    }
}
