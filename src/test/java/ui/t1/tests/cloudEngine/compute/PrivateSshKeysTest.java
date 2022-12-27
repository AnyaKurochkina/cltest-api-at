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
//    @TmsLink("")
    @Order(2)
    @DisplayName("Cloud Compute. Удаление SSH-ключа (личный)")
    void deleteKey() {
        new IndexPage().goToProfile().getSshKeys().deleteKey(name);
    }
}
