package tests.tarifficator;

import core.helper.Configure;
import core.helper.Http;
import core.utils.AssertUtils;
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

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Управление")
@Feature("Базовые тарифные планы")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@Tags({@Tag("regress"), @Tag("tariff")})
public class BaseTariffPlanTests extends Tests {
    TariffPlanSteps tariffPlanSteps = new TariffPlanSteps();

    @Test
    @Order(1)
    @DisplayName("Создание тарифного плана на базе активного")
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
                .build()
                .createObject();
        JSONObject object = TariffPlan.builder()
                .title(tariffPlan.getTitle())
                .base(true)
                .oldTariffPlanId(tariffPlan.getOldTariffPlanId())
                .build()
                .toJson();
        new Http(Configure.TarifficatorURL)
                .post("tariff_plans", object)
                .assertStatus(422);
    }

    @Test
    @Order(3)
    @DisplayName("Изменение имени базового тарифного плана в статусе черновик")
    public void renameBaseTariffPlan() {
        TariffPlan tariffPlan = TariffPlan.builder()
                .base(true)
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

}
