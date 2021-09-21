package tests.tarifficator;

import core.CacheService;
import core.helper.IEntity;
import core.helper.ObjectPoolEntity;
import core.helper.ObjectPoolService;
import core.helper.EntityUse;
import core.utils.AssertUtils;
import io.qameta.allure.TmsLink;
import models.orderService.interfaces.IProduct;
import models.orderService.products.ProductStatus;
import models.tarifficator.TariffPlan;
import models.tarifficator.TariffPlanStatus;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.tarifficator.TariffPlanSteps;
import tests.Tests;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Базовые тарифные планы")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseTariffPlanTests extends Tests {
    TariffPlanSteps tariffPlanSteps = new TariffPlanSteps();
    CacheService cacheService = new CacheService();

    @Test
    @TmsLink("34")
    @Order(1)
    @DisplayName("Создание тарифного плана на базе активного")
    public void createBaseTariffPlanFromActive() {
        Date currentDate = new Date();
        String tariffName = "AT " + currentDate;
        TariffPlan activeTariff = tariffPlanSteps.getTariffPlanList("include=tariff_classes&f[base]=true&f[status][]=active").get(0);
        String tariffPlanId = tariffPlanSteps.createTariffPlan(TariffPlan.builder()
                .title(tariffName)
                .base(true)
                .oldTariffPlanId(activeTariff.getId())
                .build()).getId();
        TariffPlan tariffPlan = tariffPlanSteps.getTariffPlan(tariffPlanId);

        assertAll("Проверка полей созданного тарифного плана",
                () -> assertNull(tariffPlan.getBeginDate()),
                () -> assertNull(tariffPlan.getEndDate()),
                () -> assertNull(tariffPlan.getOrganizationName()),
                () -> assertEquals(activeTariff.getId(), tariffPlan.getOldTariffPlanId()),
                () -> assertEquals(TariffPlanStatus.draft, tariffPlan.getStatus()),
                () -> assertEquals(tariffName, tariffPlan.getTitle()),
                () -> assertEquals(activeTariff.getTariffClasses().size(), tariffPlan.getTariffClasses().size()),
                () -> AssertUtils.AssertDate(currentDate, tariffPlan.getCreatedAt(), 300));
//        cacheService.saveEntity(tariffPlan);
    }

    @Test
    @TmsLink("35")
    @Order(2)
    @DisplayName("Создание тарифного плана с существующим именем")
    public void duplicateNameBaseTariffPlan() {
//        TariffPlan tariffPlan = cacheService.entity(TariffPlan.class)
//                .withField("status", TariffPlanStatus.draft)
//                .getEntity();
//        assertThrows(
//                Http.StatusResponseException.class,
//                () -> tariffPlanSteps.createTariffPlan(TariffPlan.builder()
//                        .title(tariffPlan.getTitle())
//                        .base(true)
//                        .oldTariffPlanId(tariffPlan.getOldTariffPlanId())
//                        .build()));
    }

    @Test
    @TmsLink("36")
    @Order(3)
    @DisplayName("Изменение имени тарифного плана в статусе черновик")
    public void renameBaseTariffPlan() {
//        TariffPlan tariffPlan = cacheService.entity(TariffPlan.class)
//                .withField("status", TariffPlanStatus.draft)
//                .getEntity();
//        String tariffName = "_RENAME " + tariffPlan.getTitle();
//        TariffPlan updateTariff = TariffPlan.builder()
//                .id(tariffPlan.getId())
//                .title(tariffName)
//                .build();
//        tariffPlanSteps.editTariffPlan(updateTariff);
//        updateTariff = tariffPlanSteps.getTariffPlan(updateTariff.getId());
//        assertEquals(tariffName, updateTariff.getTitle());
    }

    @Order(4)
    @Tag("tariffPlans")
    @Source(ProductArgumentsProvider.PRODUCTS)
    @ParameterizedTest(name = "{0}")
    @EntityUse(c = TariffPlan.class, to = ProductStatus.Num.deprovisioned)
    public void test2(IProduct p, String tmsId) throws Exception {

        try(IProduct product = p.createObjectExclusiveAccess()){
            product.runActionsAfterOtherTests();
        }



    }


    @Test
    @TmsLink("37")
    @Order(4)
//    @Tag("tariffPlans")
    @EntityUse(c = TariffPlan.class, from = TariffPlanStatus.Num.draft, to = TariffPlanStatus.Num.planned)
    public void test() throws Exception {

//        String json = "{\n" +
//                "   \"http://http://url.com/\": {\n" +
//                "      \"id\": \"http://http://url.com//\"\n" +
//                "   },\n" +
//                "   \"http://url2.co/\": {\n" +
//                "      \"id\": \"http://url2.com//\",\n" +
//                "      \"shares\": 16\n" +
//                "   }\n" +
//                "   ,\n" +
//                "   \"http://url3.com/\": {\n" +
//                "      \"id\": \"http://url3.com//\",\n" +
//                "      \"shares\": 16\n" +
//                "   }\n" +
//                "}";
//        JSONObject jsonObject = new JSONObject(json);
//        Iterator<String> keys = jsonObject.keys();
//        while(keys.hasNext()) {
//            String key = keys.next();
//            if (jsonObject.get(key) instanceof JSONObject) {
//                System.out.println(jsonObject.get(key));
//            }
//        }


        TariffPlan tariffPlan1 = TariffPlan.builder()
                .base(true)
                .build()
                .createObject();
        System.out.println(tariffPlan1.getId());

        try (TariffPlan tariffPlan2 = TariffPlan.builder()
                .base(false)
                .build()
                .createObjectExclusiveAccess())
        {
            System.out.println(tariffPlan2.getId());
        }



//        TariffPlan tariffPlan2 = TariffPlan.builder().build();
//        IEntity entity2 = objectPoolService.create(tariffPlan2, false);

        System.out.println(1);
    }
}
