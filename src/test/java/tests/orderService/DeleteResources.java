package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.IProduct;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.runner.manipulation.Ordering;

@Epic("Продукты")
@Feature("OpenShift")
@Tag("tariffPlans")
@Order(2)
@Execution(ExecutionMode.SAME_THREAD)
//@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DeleteResources {

}
