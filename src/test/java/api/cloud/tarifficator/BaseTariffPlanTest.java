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
import models.cloud.tarifficator.TariffClass;
import models.cloud.tarifficator.TariffPlan;
import models.cloud.tarifficator.TariffPlanStatus;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import steps.tarifficator.TariffPlanSteps;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Управление")
@Feature("Базовые тарифные планы")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tags({@Tag("regress"), @Tag("tariff")})
@DisabledIfEnv("prod")
public class BaseTariffPlanTest extends Tests {

    @Test
    @Order(1)
    @TmsLink("531448")
    @DisplayName("Создание тарифного плана")
    void createBaseTariffPlanFromActive() {
        TariffPlan activeTariff = TariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=true&f[status][]=active").get(0);
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(true)
                .oldTariffPlanId(activeTariff.getId())
                .build()
                .createObject();

        assertAll("Проверка полей созданного тарифного плана",
                () -> assertNull(tariffPlan.getBeginDate()),
                () -> assertNull(tariffPlan.getEndDate()),
                () -> assertNull(tariffPlan.getOrganizationName()),
                () -> assertEquals(activeTariff.getId(), tariffPlan.getOldTariffPlanId()),
                () -> assertEquals(TariffPlanStatus.draft, tariffPlan.getStatus()),
                () -> assertEquals(activeTariff.getTariffClasses().size(), tariffPlan.getTariffClasses().size()),
                () -> AssertUtils.AssertDate(new Date(), tariffPlan.getCreatedAt(), 300, "Время создания ТП не соответствует текущему"));
    }

    @Test
    @Order(2)
    @TmsLink("650114")
    @DisplayName("Создание базового тарифного плана с существующим именем")
    void duplicateNameBaseTariffPlan() {
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(true)
                .status(TariffPlanStatus.draft)
                .build()
                .createObject();
        JSONObject object = TariffPlan.builder()
                .title(tariffPlan.getTitle())
                .base(true)
                .oldTariffPlanId(tariffPlan.getOldTariffPlanId())
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
    @TmsLink("531461")
    @DisplayName("Черновик. Изменение имени тарифного плана")
    public void renameBaseTariffPlan() {
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(true)
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
    @TmsLink("531500")
    @DisplayName("Черновик -> Планируемый")
    public void tariffPlanToPlanned() {
        toPlannedOrToDraft();
    }

    @Test
    @Order(5)
    @TmsLink("723953")
    @DisplayName("Планируемый -> Черновик")
    public void tariffPlanToDraft() {
        toPlannedOrToDraft();
    }

    public void toPlannedOrToDraft() {
        Date date = new CustomDate((Calendar.getInstance().getTimeInMillis() + (16 * 60 * 1000)));
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(true)
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
    @Test
    @TmsLink("531464")
    @DisplayName("Активация и Архивация")
    public void activateBaseTariffPlan() {
        Date date = new CustomDate((Calendar.getInstance().getTimeInMillis() + (16 * 60 * 1000)));
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(true)
                .status(TariffPlanStatus.draft)
                .build()
                .createObject();
        TariffPlan activeTariff = TariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=true&f[status][]=active").get(0);

        tariffPlan.setStatus(TariffPlanStatus.planned);
        tariffPlan.setBeginDate(date);
        tariffPlan = TariffPlanSteps.editTariffPlan(tariffPlan);
        Waiting.sleep(17 * 60 * 1000);
        TariffPlan updatedTariffPlan = TariffPlanSteps.getTariffPlan(tariffPlan.getId());
        TariffPlan archiveTariff = TariffPlanSteps.getTariffPlan(activeTariff.getId());

        Assertions.assertAll("Проверка полей активного и архивного ТП",
                () -> AssertUtils.AssertDate(date, archiveTariff.getEndDate(), 60 * 15, "Время архивации ТП не соответствует действительному"),
                () -> assertEquals(TariffPlanStatus.active, updatedTariffPlan.getStatus(), "Тарифный план не перешел в статус активный"),
                () -> assertEquals(TariffPlanStatus.archived, archiveTariff.getStatus(), "Тарифный план не перешел в статус архивный"));
    }

    @Test
    @Order(7)
    @TmsLink("726835")
    @DisplayName("Редактировать ТК")
    public void editTariffClass() {
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(true)
                .status(TariffPlanStatus.draft)
                .build()
                .createObject();

        TariffClass tariffClass = tariffPlan.getTariffClasses().get(0);
        tariffClass.setPrice(tariffClass.getPrice() + 1.0f);
        TariffClass updatedTariffClass = TariffPlanSteps.editTariffClass(tariffClass, tariffPlan);
        Assertions.assertEquals(tariffClass.getPrice(), updatedTariffClass.getPrice(), "Стоимость не изменилась");
    }

    @Test
    @Order(8)
    @TmsLink("783813")
    @DisplayName("Фильтр по статусу таблицы ТП")
    public void filterTariffPlanByStatus() {
        List<TariffPlan> tariffPlans = TariffPlanSteps
                .getTariffPlanList("f[base]=true&f[status][]=" + TariffPlanStatus.draft);
        Assertions.assertAll(
                () -> Assertions.assertEquals(tariffPlans.stream().filter(tariffPlan -> tariffPlan.getStatus().equals(TariffPlanStatus.draft)).count(),
                                tariffPlans.size(), "Не все ТП в статусе draft"),
                () -> Assertions.assertEquals(tariffPlans.stream().filter(tariffPlan -> tariffPlan.getBase().equals(true)).count(),
                        tariffPlans.size(), "Не все ТП в базовые")
        );
    }

}
