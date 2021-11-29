package tests.orderService.oldProducts;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import models.orderService.interfaces.ProductStatus;
import models.orderService.products.ApacheKafkaCluster;
import models.orderService.products.OpenShiftProject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import tests.Tests;


@Epic("Старые продукты")
@Feature("OpenShift OLD")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("old_openshift"), @Tag("prod"), @Tag("old")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OldOpenShiftTest extends Tests {

    OpenShiftProject openShift = OpenShiftProject.builder()
            .projectId("proj-67nljbzjtt")
            .productId("10000003-1111-2222-b9dc-4a9e5dafd44e")
            .orderId("a4335c3c-fa1a-49ba-b528-170885e06605")
            .resourcePoolLabel("ds1-genr01.corp.dev.vtb - DEV-SRV-APP")
            .productName("OpenShift project")
            .build();

    @Order(1)
    @DisplayName("Изменить проект OpenShift OLD")
    @Test
    void change() {
        openShift.changeProject();
    }
}
