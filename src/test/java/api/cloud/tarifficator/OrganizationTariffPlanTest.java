package api.cloud.tarifficator;

import api.Tests;
import core.enums.Role;
import core.helper.Configure;
import core.helper.CustomDate;
import core.helper.http.Http;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Organization;
import models.cloud.orderService.products.Astra;
import models.cloud.tarifficator.TariffClass;
import models.cloud.tarifficator.TariffPlan;
import models.cloud.tarifficator.TariffPlanStatus;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.orderService.OrderServiceSteps;
import steps.tarifficator.TariffPlanSteps;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Управление")
@Feature("Тарифные планы организации")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tags({@Tag("regress"), @Tag("tariff")})
@DisabledIfEnv("prod")
public class OrganizationTariffPlanTest extends Tests {

    @Test
    @Order(1)
    @TmsLink("531445")
    @DisplayName("Создание тарифного плана")
    void createOrganizationTariffPlanFromActive() {
        Organization organization = Organization.builder().type("default").build().createObject();
        TariffPlan activeTariff = TariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=true&f[status][]=active").get(0);
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
                () -> AssertUtils.AssertDate(new Date(), tariffPlan.getCreatedAt(), 300, "Время создания ТП не соответствует текущему"));
    }

    @Test
    @Order(2)
    @TmsLink("531453")
    @DisplayName("Уникальность названия ТП")
    void duplicateNameBaseTariffPlan() {
        Organization organization = Organization.builder().type("default").build().createObject();
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
        new Http(Configure.tarifficatorURL)
                .setRole(Role.TARIFFICATOR_ADMIN)
                .body(object)
                .post("/v1/tariff_plans")
                .assertStatus(422);
    }

    @Test
    @Order(3)
    @TmsLink("531459")
    @DisplayName("Черновик. Изменение имени тарифного плана")
    void renameTariffPlan() {
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
        TariffPlanSteps.editTariffPlan(updateTariff);
        updateTariff = TariffPlanSteps.getTariffPlan(updateTariff.getId());
        assertEquals(tariffName, updateTariff.getTitle());
    }

    @Test
    @Order(4)
    @TmsLink("531476")
    @DisplayName("Черновик -> Планируемый")
    void tariffPlanToPlanned() {
        toPlannedOrToDraft();
    }

    @Test
    @Order(5)
    @TmsLink("725939")
    @DisplayName("Планируемый -> Черновик")
    void tariffPlanToDraft() {
        toPlannedOrToDraft();
    }

    public void toPlannedOrToDraft() {
        Date date = new CustomDate((Calendar.getInstance().getTimeInMillis() + (16 * 60 * 1000)));
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(false)
                .status(TariffPlanStatus.draft)
                .build()
                .createObject();
        tariffPlan.setStatus(TariffPlanStatus.planned);
        tariffPlan.setBeginDate(date);
        tariffPlan = TariffPlanSteps.editTariffPlan(tariffPlan);
        Assertions.assertEquals(TariffPlanStatus.planned, tariffPlan.getStatus(), String.format("Статус тарифного: %s, плана не соответсвует ожидаемому", tariffPlan.getStatus()));

        tariffPlan.setStatus(TariffPlanStatus.draft);
        TariffPlanSteps.editTariffPlan(tariffPlan);
    }

    @Order(6)
    @TmsLink("531467")
    @ParameterizedTest(name = "Активация и Архивация (без update_orders)")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    void activateTariffPlanWithoutUpdateOrders(Astra product) {
        String tariffPlanIdPath = "attrs.tariff_plan_id";
        try (Astra rhel = product.createObjectExclusiveAccess()) {
            String tariffPlanId = ((String) OrderServiceSteps.getProductsField(rhel, tariffPlanIdPath));
            Date date = new CustomDate((Calendar.getInstance().getTimeInMillis() + (16 * 60 * 1000)));
            TariffPlan tariffPlan = TariffPlan.builder()
                    .base(false)
                    .status(TariffPlanStatus.draft)
                    .build()
                    .createObject();
            Organization organization = Organization.builder().type("default").build().createObject();
            TariffPlan activeTariff = TariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=false&f[organization_name]=" + organization.getName() + "&f[status][]=active").get(0);

            tariffPlan.setStatus(TariffPlanStatus.planned);
            tariffPlan.setBeginDate(date);
            tariffPlan = TariffPlanSteps.editTariffPlan(tariffPlan);
            Waiting.sleep(16 * 60 * 1000);
            TariffPlan updatedTariffPlan = TariffPlanSteps.getTariffPlan(tariffPlan.getId());
            TariffPlan archiveTariff = TariffPlanSteps.getTariffPlan(activeTariff.getId());

            Assertions.assertAll("Проверка полей активного и архивного ТП",
                    () -> AssertUtils.AssertDate(date, archiveTariff.getEndDate(), 60 * 15, "Время архивации ТП не соответствует действительному"),
                    () -> assertEquals(TariffPlanStatus.active, updatedTariffPlan.getStatus(), "Тарифный план не перешел в статус активный"),
                    () -> assertEquals(TariffPlanStatus.archived, archiveTariff.getStatus(), "Тарифный план не перешел в статус архивный"),
                    () -> assertEquals(tariffPlanId, OrderServiceSteps.getProductsField(rhel, tariffPlanIdPath), "Тарифный план у продукта изменился"));
        }
    }

    @Order(7)
    @TmsLink("531468")
    @ParameterizedTest(name = "Активация и Архивация (с update_orders)")
    @Source(ProductArgumentsProvider.ONE_PRODUCT)
    void activateTariffPlanWithUpdateOrders(Astra product) {
        String tariffPlanIdPath = "attrs.tariff_plan_id";
        try (Astra rhel = product.createObjectExclusiveAccess()) {
            Date date = new CustomDate((Calendar.getInstance().getTimeInMillis() + (16 * 60 * 1000)));
            TariffPlan tariffPlan = TariffPlan.builder()
                    .base(false)
                    .status(TariffPlanStatus.draft)
                    .build()
                    .createObject();
            Organization organization = Organization.builder().type("default").build().createObject();
            TariffPlan activeTariff = TariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=false&f[organization_name]=" + organization.getName() + "&f[status][]=active").get(0);

            tariffPlan.setStatus(TariffPlanStatus.planned);
            tariffPlan.setUpdateOrders(true);
            tariffPlan.setBeginDate(date);
            tariffPlan = TariffPlanSteps.editTariffPlan(tariffPlan);
            Waiting.sleep(17 * 60 * 1000);
            TariffPlan updatedTariffPlan = TariffPlanSteps.getTariffPlan(tariffPlan.getId());
            TariffPlan archiveTariff = TariffPlanSteps.getTariffPlan(activeTariff.getId());

            Assertions.assertAll("Проверка полей активного и архивного ТП",
                    () -> AssertUtils.AssertDate(date, archiveTariff.getEndDate(), 60 * 15, "Время архивации ТП не соответствует действительному"),
                    () -> assertEquals(TariffPlanStatus.active, updatedTariffPlan.getStatus(), "Тарифный план не перешел в статус активный"),
                    () -> assertEquals(TariffPlanStatus.archived, archiveTariff.getStatus(), "Тарифный план не перешел в статус архивный"),
                    () -> assertEquals(updatedTariffPlan.getId(), OrderServiceSteps.getProductsField(rhel, tariffPlanIdPath), "Тарифный план у продукта не изменился"));
        }
    }

    @Test
    @Order(8)
    @TmsLink("729753")
    @DisplayName("Редактировать ТК")
    public void editTariffClass() {
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(false)
                .status(TariffPlanStatus.draft)
                .build()
                .createObject();

        TariffClass tariffClass = tariffPlan.getTariffClasses().get(0);
        tariffClass.setPrice(tariffClass.getPrice() + 1.0f);
        TariffClass updatedTariffClass = TariffPlanSteps.editTariffClass(tariffClass, tariffPlan);
        Assertions.assertEquals(tariffClass.getPrice(), updatedTariffClass.getPrice(), "Стоимость не изменилась");
    }

    @Test
    @Order(9)
    @TmsLink("783797")
    @DisplayName("Фильтр по статусу таблицы ТП")
    public void filterTariffPlanByStatus() {
        Organization organization = Organization.builder().type("default").build().createObject();
        List<TariffPlan> tariffPlans = TariffPlanSteps
                .getTariffPlanList("f[base]=false&f[organization_name]=" + organization.getName() + "&f[status][]=" + TariffPlanStatus.draft);
        Assertions.assertAll(
                () -> Assertions.assertEquals(tariffPlans.stream().filter(tariffPlan -> tariffPlan.getStatus().equals(TariffPlanStatus.draft)).count(),
                        tariffPlans.size(), "Не все ТП в статусе draft"),
                () -> Assertions.assertEquals(tariffPlans.stream().filter(tariffPlan -> tariffPlan.getBase().equals(false)).count(),
                        tariffPlans.size(), "В списке присутствуют БТП")
        );
    }
}
