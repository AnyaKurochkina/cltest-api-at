package tests.tarifficator;

import core.helper.Configure;
import core.helper.CustomDate;
import core.helper.Http;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.tarifficator.TariffPlan;
import models.tarifficator.TariffPlanStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.tarifficator.TariffPlanSteps;
import tests.Tests;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Управление")
@Feature("Базовые тарифные планы")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@Tags({@Tag("regress"), @Tag("tariff")})
public class BaseTariffPlanTest extends Tests {
    TariffPlanSteps tariffPlanSteps = new TariffPlanSteps();

    @Test
    @Order(1)
    @DisplayName("Создание тарифного плана")
    void createBaseTariffPlanFromActive() {
        TariffPlan activeTariff = tariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=true&f[status][]=active").get(0);
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
                () -> AssertUtils.AssertDate(new Date(), tariffPlan.getCreatedAt(), 300));
    }

    @Test
    @Order(2)
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
        new Http(Configure.TarifficatorURL)
                .body(object)
                .post("tariff_plans")
                .assertStatus(422);
    }

    @Test
    @Order(3)
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
                .base(true)
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
    @Test
    @DisplayName("Активация и Архивация")
    public void activateBaseTariffPlan() {
        Date date = new CustomDate((Calendar.getInstance().getTimeInMillis() + (16 * 60 * 1000)));
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(true)
                .status(TariffPlanStatus.draft)
                .build()
                .createObject();
        TariffPlan activeTariff = tariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=true&f[status][]=active").get(0);

        tariffPlan.setStatus(TariffPlanStatus.planned);
        tariffPlan.setBeginDate(date);
        tariffPlan = tariffPlanSteps.editTariffPlan(tariffPlan);
        Waiting.sleep(15 * 60 * 1000);
        TariffPlan updatedTariffPlan = tariffPlanSteps.getTariffPlan(tariffPlan.getId());
        TariffPlan archiveTariff = tariffPlanSteps.getTariffPlan(activeTariff.getId());

        Assertions.assertAll(
                () -> AssertUtils.AssertDate(date, archiveTariff.getEndDate(), 60 * 15),
                () -> assertEquals(TariffPlanStatus.active, updatedTariffPlan.getStatus(), "Тарифный план не перешел в статус активный"),
                () -> assertEquals(TariffPlanStatus.archived, archiveTariff.getStatus(), "Тарифный план не перешел в статус архивный"));
    }

}
