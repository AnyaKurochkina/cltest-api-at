package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.orderService.products.Windows;
import org.junit.jupiter.api.*;
import tests.Tests;

import static models.orderService.interfaces.ProductStatus.STOPPED;
import static models.orderService.interfaces.ProductStatus.STARTED;

@Epic("Старые продукты DEV")
@Feature("Windows OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_windows"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldWindowsTest extends Tests {

    final Windows windows = Windows.builder()
            .projectId("proj-rddf0uwi0q")
            .productId("28bed880-2714-4317-a967-d000d492bd9d")
            .orderId("f668d95b-5633-4670-afd0-586ea059f7b2")
            .productName("Windows")
            .build();

    @Order(1)
    @TmsLink("841702")
    @DisplayName("Перезагрузить Windows OLD")
    @Test
    void restart() {
        if (windows.productStatusIs(STOPPED)) {
            windows.start();
        }
        windows.restart();
    }

    @Order(2)
    @TmsLink("841705")
    @DisplayName("Выключить Windows OLD")
    @Test
    void stopSoft() {
        if (windows.productStatusIs(STOPPED)) {
            windows.start();
        }
        windows.stopSoft();
    }

    @Order(3)
    @TmsLink("841707")
    @DisplayName("Изменить конфигурацию Windows OLD")
    @Test
    void resize() {
        if (windows.productStatusIs(STARTED)) {
            windows.stopHard();
        }
        windows.resize(windows.getMaxFlavor());
        windows.resize(windows.getMinFlavor());
    }

    @Order(4)
    @TmsLink("841704")
    @DisplayName("Включить Windows OLD")
    @Test
    void start() {
        if (windows.productStatusIs(STARTED)) {
            windows.stopHard();
        }
        windows.start();
    }

//    @Order(5)
//    @TmsLink("841708")
//    @DisplayName("Добавить диск Windows OLD")
//    @Test
//    void addDisk() {
//        if (windows.productStatusIs(STOPPED)) {
//            windows.start();
//        }
//        windows.addDisk("I");
//        windows.unmountDisk("I");
//        windows.deleteDisk("I");
//    }

    @Order(6)
    @TmsLink("841709")
    @DisplayName("Проверить конфигурацию Windows OLD")
    @Test
    void refreshVmConfig() {
        if (windows.productStatusIs(STOPPED)) {
            windows.start();
        }
        windows.refreshVmConfig();
    }

    @Order(7)
    @TmsLink("841710")
    @DisplayName("Удалить диск Windows OLD")
    @Test
    void deleteDisk() {
        if (windows.productStatusIs(STOPPED)) {
            windows.start();
        }
        windows.addDisk("L");
        windows.unmountDisk("L");
        windows.mountDisk("L");
        windows.unmountDisk("L");
        windows.deleteDisk("L");
    }

//    @Order(8)
//    @TmsLink("841711")
//    @DisplayName("Подключить диск Windows OLD")
//    @Test
//    void mountDisk() {
//        if (windows.productStatusIs(STOPPED)) {
//            windows.start();
//        }
//        windows.addDisk("S");
//        windows.unmountDisk("S");
//        windows.mountDisk("S");
//    }

//    @Order(9)
//    @TmsLink("841712")
//    @DisplayName("Отключить диск Windows OLD")
//    @Test
//    void unmountDisk() {
//        if (windows.productStatusIs(STOPPED)) {
//            windows.start();
//        }
//        windows.addDisk("T");
//        windows.unmountDisk("T");
//        windows.deleteDisk("T");
//    }

//   @Order(10)
//    @TmsLink("841700")
//    @DisplayName("Расширить диск Windows OLD")
//    @Test
//    void expandDisk() {
//        if (windows.productStatusIs(STOPPED)) {
//            windows.start();
//        }
//       windows.addDisk("K");
//       windows.expandMountPoint("K");
//    }

    @Order(11)
    @TmsLink("841703")
    @DisplayName("Выключить принудительно Windows OLD")
    @Test
    void stopHard() {
        if (windows.productStatusIs(STOPPED)) {
            windows.start();
        }
        windows.stopHard();
    }
}
