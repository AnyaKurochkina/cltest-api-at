package tests.orderService.oldProducts.dev;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.orderService.products.OpenShiftProject;
import models.subModels.Role;
import org.junit.jupiter.api.*;
import tests.Tests;

import java.util.Collections;


@Epic("Старые продукты")
@Feature("OpenShift OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_openshift"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldOpenShiftTest extends Tests {

    final OpenShiftProject openShift = OpenShiftProject.builder()
            .projectId("proj-67nljbzjtt")
            .productId("10000003-1111-2222-b9dc-4a9e5dafd44e")
            .orderId("a4335c3c-fa1a-49ba-b528-170885e06605")
            .resourcePoolLabel("ds1-genr01.corp.dev.vtb - DEV-SRV-APP")
            .roles(Collections.singletonList(new Role("edit", "cloud-dhzorg-123")))
            .productName("OpenShift project")
            .build();

    @Order(1)
    @DisplayName("Изменить проект OpenShift OLD")
    @Test
    void change() {
        openShift.changeProject();
    }
}
