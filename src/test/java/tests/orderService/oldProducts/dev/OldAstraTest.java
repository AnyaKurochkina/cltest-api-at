package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.Astra;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STARTED;
import static models.orderService.interfaces.ProductStatus.STOPPED;

@Epic("Старые продукты DEV")
@Feature("Astra OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_astra"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldAstraTest extends Tests {

    final Astra astra = Astra.builder()
            .projectId("proj-67nljbzjtt")
            .productId("c0aa15f7-5854-4c93-8c0f-1fcc9566f783")
            .orderId("b5998259-ccab-4a98-949d-801b115ec180")//43c2f7f3-74e2-4f78-beef-aae28107b6a1 создал новый(старый бажный)
            .productName("Astra")
            .build();


    @Order(1)
    @DisplayName("Расширить Astra OLD")
    @Test
    void expandMountPoint() {
        if (astra.productStatusIs(STOPPED)) {
            astra.start();
        }
        astra.expandMountPoint();
    }

    @Order(2)
    @DisplayName("Перезагрузить Astra OLD")
    @Test
    void restart() {
        if (astra.productStatusIs(STOPPED)) {
            astra.start();
        }
        astra.restart();
    }

    @Order(3)
    @DisplayName("Выключить Astra OLD")
    @Test
    void stopSoft() {
        if (astra.productStatusIs(STOPPED)) {
            astra.start();
        }
        astra.stopSoft();
    }

    @Order(4)
    @DisplayName("Изменить конфигурацию Astra OLD")
    @Test
    void resize() {
        if (astra.productStatusIs(STARTED)) {
            astra.stopHard();
        }
        astra.resize(astra.getMaxFlavor());
        astra.resize(astra.getMinFlavor());
    }

    @Order(5)
    @DisplayName("Включить Astra OLD")
    @Test
    void start() {
        if (astra.productStatusIs(STARTED)) {
            astra.stopHard();
        }
        astra.start();
    }

    @Order(6)
    @DisplayName("Выключить принудительно Astra OLD")
    @Test
    void stopHard() {
        if (astra.productStatusIs(STOPPED)) {
            astra.start();
        }
        astra.stopHard();
    }
}
