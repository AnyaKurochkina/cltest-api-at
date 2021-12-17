package tests.tarifficator;

import core.helper.Configure;
import core.helper.CustomDate;
import core.helper.Http;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.authorizer.Organization;
import models.orderService.products.Rhel;
import models.tarifficator.TariffPlan;
import models.tarifficator.TariffPlanStatus;
import org.json.JSONObject;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import steps.tarifficator.TariffPlanSteps;
import tests.Tests;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Управление")
@Feature("Тарифные планы организации")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@Tags({@Tag("regress"), @Tag("tariff")})
public class OrganizationTariffPlanTest extends Tests {
    TariffPlanSteps tariffPlanSteps = new TariffPlanSteps();
    OrderServiceSteps orderServiceSteps = new OrderServiceSteps();

    @Test
    @Order(1)
    @DisplayName("Создание тарифного плана")
    public void createOrganizationTariffPlanFromActive() {
        Organization organization = Organization.builder().build().createObject();
        TariffPlan activeTariff = tariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=true&f[status][]=active").get(0);
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(false)
                .oldTariffPlanId(activeTariff.getId())
                .organizationName(organization.getName())
                .build()
                .createObject();

        assertAll("Проверка полей созданного тарифного плана",
                () -> assertNull(tariffPlan.getBeginDate()),
                () -> assertNull(tariffPlan.getEndDate()),
                () -> assertEquals(organization.getName(), tariffPlan.getOrganizationName()),
                () -> assertEquals(activeTariff.getId(), tariffPlan.getOldTariffPlanId()),
                () -> assertEquals(TariffPlanStatus.draft, tariffPlan.getStatus()),
                () -> assertEquals(activeTariff.getTariffClasses().size(), tariffPlan.getTariffClasses().size()),
                () -> AssertUtils.AssertDate(new Date(), tariffPlan.getCreatedAt(), 300));
    }

    @Test
    @Order(2)
    @DisplayName("Создание базового тарифного плана с существующим именем")
    void duplicateNameBaseTariffPlan() {
        Organization organization = Organization.builder().build().createObject();
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(false)
                .status(TariffPlanStatus.draft)
                .organizationName(organization.getName())
                .build()
                .createObject();
        JSONObject object = TariffPlan.builder()
                .title(tariffPlan.getTitle())
                .base(false)
                .oldTariffPlanId(tariffPlan.getOldTariffPlanId())
                .organizationName(organization.getName())
                .build()
                .toJson();
        new Http(Configure.TarifficatorURL)
                .body(object)
                .post("tariff_plans")
                .assertStatus(422);
    }

    @Test
    @Order(3)
    @DisplayName("Черновик. Изменение имени тарифного плана")
    public void renameTariffPlan() {
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(false)
                .status(TariffPlanStatus.draft)
                .build()
                .createObject();
        String tariffName = "_RENAME " + tariffPlan.getTitle();
        TariffPlan updateTariff = TariffPlan.builder()
                .id(tariffPlan.getId())
                .title(tariffName)
                .build();
        tariffPlanSteps.editTariffPlan(updateTariff);
        updateTariff = tariffPlanSteps.getTariffPlan(updateTariff.getId());
        assertEquals(tariffName, updateTariff.getTitle());
    }

    @Test
    @Order(4)
    @DisplayName("Черновик -> Планируемый")
    public void tariffPlanToPlanned() {
        Date date = new CustomDate((Calendar.getInstance().getTimeInMillis() + (16 * 60 * 1000)));
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(false)
                .status(TariffPlanStatus.draft)
                .build()
                .createObject();
        tariffPlan.setStatus(TariffPlanStatus.planned);
        tariffPlan.setBeginDate(date);
        tariffPlan = tariffPlanSteps.editTariffPlan(tariffPlan);
        Assertions.assertEquals(TariffPlanStatus.planned, tariffPlan.getStatus(), String.format("Статус тарифного: %s, плана не соответсвует ожидаемому", tariffPlan.getStatus()));

        tariffPlan.setStatus(TariffPlanStatus.draft);
        tariffPlanSteps.editTariffPlan(tariffPlan);
    }

    @Order(5)
    @ParameterizedTest(name = "Активация и Архивация (без update_orders)")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    public void activateTariffPlanWithoutUpdateOrders(Rhel product) {
        String tariffPlanIdPath = "attrs.tariff_plan_id";
        Rhel rhel = product.createObject();
        String tariffPlanId = ((String) orderServiceSteps.getProductsField(rhel, tariffPlanIdPath));
        Date date = new CustomDate((Calendar.getInstance().getTimeInMillis() + (16 * 60 * 1000)));
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(false)
                .status(TariffPlanStatus.draft)
                .build()
                .createObject();
        Organization organization = Organization.builder().build().createObject();
        TariffPlan activeTariff = tariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=false&f[organization_name]=" + organization.getName() + "&f[status][]=active").get(0);

        tariffPlan.setStatus(TariffPlanStatus.planned);
        tariffPlan.setBeginDate(date);
        tariffPlan = tariffPlanSteps.editTariffPlan(tariffPlan);
        Waiting.sleep(15 * 60 * 1000);
        TariffPlan updatedTariffPlan = tariffPlanSteps.getTariffPlan(tariffPlan.getId());
        TariffPlan archiveTariff = tariffPlanSteps.getTariffPlan(activeTariff.getId());

        Assertions.assertAll(
                () -> AssertUtils.AssertDate(date, archiveTariff.getEndDate(), 60 * 15),
                () -> assertEquals(TariffPlanStatus.active, updatedTariffPlan.getStatus(), "Тарифный план не перешел в статус активный"),
                () -> assertEquals(TariffPlanStatus.archived, archiveTariff.getStatus(), "Тарифный план не перешел в статус архивный"),
                () -> assertEquals(tariffPlanId, orderServiceSteps.getProductsField(rhel, tariffPlanIdPath), "Тарифный план у продукта изменился"));
    }

    @Order(6)
    @ParameterizedTest(name = "Активация и Архивация (с update_orders)")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    public void activateTariffPlanWithUpdateOrders(Rhel product) {
        String tariffPlanIdPath = "attrs.tariff_plan_id";
        Rhel rhel = product.createObject();
        String tariffPlanId = ((String) orderServiceSteps.getProductsField(rhel, tariffPlanIdPath));
        Date date = new CustomDate((Calendar.getInstance().getTimeInMillis() + (16 * 60 * 1000)));
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(false)
                .status(TariffPlanStatus.draft)
                .build()
                .createObject();
        Organization organization = Organization.builder().build().createObject();
        TariffPlan activeTariff = tariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=false&f[organization_name]=" + organization.getName() + "&f[status][]=active").get(0);

        tariffPlan.setStatus(TariffPlanStatus.planned);
        tariffPlan.setUpdateOrders(true);
        tariffPlan.setBeginDate(date);
        tariffPlan = tariffPlanSteps.editTariffPlan(tariffPlan);
        Waiting.sleep(15 * 60 * 1000);
        TariffPlan updatedTariffPlan = tariffPlanSteps.getTariffPlan(tariffPlan.getId());
        TariffPlan archiveTariff = tariffPlanSteps.getTariffPlan(activeTariff.getId());

        Assertions.assertAll(
                () -> AssertUtils.AssertDate(date, archiveTariff.getEndDate(), 60 * 15),
                () -> assertEquals(TariffPlanStatus.active, updatedTariffPlan.getStatus(), "Тарифный план не перешел в статус активный"),
                () -> assertEquals(TariffPlanStatus.archived, archiveTariff.getStatus(), "Тарифный план не перешел в статус архивный"),
                () -> assertNotEquals(tariffPlanId, orderServiceSteps.getProductsField(rhel, tariffPlanIdPath), "Тарифный план у продукта не изменился"));
    }
}
