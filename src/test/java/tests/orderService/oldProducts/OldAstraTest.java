package tests.orderService.oldProducts;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Astra;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Старые продукты")
@Feature("Astra OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_astra"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldAstraTest extends Tests {

    Astra astra = Astra.builder()
            .projectId("proj-67nljbzjtt")
            .productId("b5998259-ccab-4a98-949d-801b115ec180")
            .orderId("b5998259-ccab-4a98-949d-801b115ec180")//43c2f7f3-74e2-4f78-beef-aae28107b6a1 создал новый(старый бажный)
            .productName("Astra")
            .build();


    @Order(1)
    @DisplayName("Расширить Astra OLD")
    @Test
    void expandMountPoint() {
        astra.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Перезагрузить Astra OLD")
    @Test
    void restart() {
        astra.restart();
    }

    @Order(3)
    @DisplayName("Выключить Astra OLD")
    @Test
    void stopSoft() {
        astra.stopSoft();
        astra.start();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию Astra OLD")
    @Test
    void resize() {
        astra.stopHard();
        try {
            astra.resize();
        } finally {
            astra.start();
        }
    }

    @Order(5)
    @DisplayName("Включить Astra OLD")
    @Test
    void start() {
        astra.stopHard();
        astra.start();
    }

    @Order(6)
    @DisplayName("Выключить принудительно Astra OLD")
    @Test
    void stopHard() {
        astra.stopHard();
        astra.start();
    }
}
