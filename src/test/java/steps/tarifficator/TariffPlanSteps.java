package steps.tarifficator;

import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import models.tarifficator.TariffPlan;
import steps.Steps;

@Log4j2
public class TariffPlanSteps extends Steps {
    public static final String URL = Configure.getAppProp("host_kong") + "tarifficator/api/v1/";

    @Step("Создание тарифного плана {tariffPlan}")
    public TariffPlan createTariffPlan(TariffPlan tariffPlan){
        String object = new Http(URL)
                .post("tariff_plans", tariffPlan.serialize())
                .assertStatus(201)
                .toString();
        return TariffPlan.deserialize(object);
    }
}
