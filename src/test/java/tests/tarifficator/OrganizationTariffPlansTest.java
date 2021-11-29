package tests.tarifficator;

import core.CacheService;
import core.helper.CustomDate;
import core.utils.AssertUtils;
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

@DisplayName("Базовые тарифные планы")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@Tag("OTP")
public class OrganizationTariffPlansTest extends Tests {
    TariffPlanSteps tariffPlanSteps = new TariffPlanSteps();
    CacheService cacheService = new CacheService();

    @Test
    @Order(1)
    @DisplayName("Создание тарифного плана на базе активного")
    public void createOrganizationTariffPlanFromActive() {
        Organization organization = cacheService.entity(Organization.class).getEntity();
//        Organization organization = Organization.builder().build().createObject();
        Date currentDate = new Date();
        String tariffName = "AT " + currentDate;
        TariffPlan activeTariff = tariffPlanSteps.getTariffPlanList(String.format("include=tariff_classes&f[base]=%s&f[status][]=%s",
                "false",
                "active")).get(0);
        String tariffPlanId = tariffPlanSteps.createTariffPlan(TariffPlan.builder()
                .title(tariffName)
                .base(false)
                .oldTariffPlanId(activeTariff.getId())
                .organizationName(organization.name)
                .build()).getId();
        TariffPlan tariffPlan = tariffPlanSteps.getTariffPlan(tariffPlanId);

        assertAll("Проверка полей созданного тарифного плана",
                () -> assertNull(tariffPlan.getBeginDate()),
                () -> assertNull(tariffPlan.getEndDate()),
                () -> assertEquals(organization.name, tariffPlan.getOrganizationName()),
                () -> assertEquals(activeTariff.getId(), tariffPlan.getOldTariffPlanId()),
                () -> assertEquals(TariffPlanStatus.draft, tariffPlan.getStatus()),
                () -> assertEquals(tariffName, tariffPlan.getTitle()),
                () -> assertEquals(activeTariff.getTariffClasses().size(), tariffPlan.getTariffClasses().size()),
                () -> AssertUtils.AssertDate(currentDate, tariffPlan.getCreatedAt(), 300));
        cacheService.saveEntity(tariffPlan);
    }

    @Test
    @TmsLink("39")
    @Order(2)
    @DisplayName("Изменение имени тарифного плана в статусе черновик")
    public void renameBaseTariffPlan() {
        Date date = new CustomDate((Calendar.getInstance().getTimeInMillis() + (16 * 60 * 1000)));
        TariffPlan tariffPlan = cacheService.entity(TariffPlan.class)
                .withField("status", TariffPlanStatus.draft)
                .withField("base", false)
                .getEntity();
        tariffPlan.setStatus(TariffPlanStatus.planned);
        tariffPlan.setBeginDate(date);
        tariffPlan = tariffPlanSteps.editTariffPlan(tariffPlan);
        Assertions.assertEquals(TariffPlanStatus.planned, tariffPlan.getStatus(), String.format("Статус тарифного: %s, плана не соответсвует ожидаемому", tariffPlan.getStatus()));
    }
}
