package tests.tarifficator;

import core.helper.CustomDate;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.authorizer.Organization;
import models.tarifficator.TariffPlan;
import models.tarifficator.TariffPlanStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
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

    @Test
    @Order(1)
    @DisplayName("Создание тарифного плана на базе активного")
    public void createOrganizationTariffPlanFromActive() {
        Organization organization = Organization.builder().build().createObject();
        TariffPlan activeTariff = tariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=false&f[status][]=active").get(0);
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
    @DisplayName("Изменение имени тарифного плана в статусе черновик")
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
    @Order(3)
    @DisplayName("Изменение имени тарифного плана в статусе черновик")
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
    }
}
