package steps.tarifficator;

import com.google.gson.reflect.TypeToken;
import core.CacheService;
import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import models.tarifficator.TariffPlan;
import org.json.JSONArray;
import steps.Steps;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
public class TariffPlanSteps extends Steps {
    public static final String URL = Configure.getAppProp("host_kong") + "tarifficator/api/v1/";

    public TariffPlan deserialize(String object) {
        return CacheService.getCustomGson().fromJson(object, TariffPlan.class);
    }

    /**
     * Отправка запроса на создание ТП
     *
     * @param tariffPlan объект класса {@code TariffPlan}, где заполнены поля {@code oldTariffPlanId, title, base}
     * @return созданный ТП
     */
    @Step("Создание тарифного плана {tariffPlan}")
    public TariffPlan createTariffPlan(TariffPlan tariffPlan) {
        String object = new Http(URL)
                .post("tariff_plans", tariffPlan.toJson())
                .assertStatus(201)
                .toString();
        return deserialize(object);
    }

    /**
     * Получение списка ТП с указанными параметрами
     *
     * @param urlParameters строка GET параметров, по которым осуществится выборка, например {@code "f[base]=true&f[status][]=active"}
     * @return список тарифных планов соответствующих urlParameters
     */
    @Step("Получение списка тарифных планов c параметрами '{urlParameters}'")
    public List<TariffPlan> getTariffPlanList(String urlParameters) {
        Type type = new TypeToken<List<TariffPlan>>() {
        }.getType();
        List<Object> allResponseList = new ArrayList<>();
        List<Object> responseList;
        int i = 1;
        do {
            responseList = new Http(URL)
                    .get(String.format("tariff_plans?page=%d&per_page=100&%s", i, urlParameters))
                    .assertStatus(200)
                    .jsonPath()
                    .getList("list");
            allResponseList.addAll(responseList);
            i++;
        } while (responseList.size() > 0);
        return CacheService.getCustomGson().fromJson(new JSONArray(allResponseList).toString(), type);
    }

    /**
     * Отправка запроса на получение ТП
     *
     * @param tariffPlanId {@code id} тарифного плана
     * @return запрашиваемый ТП
     */
    @Step("Получение тарифного плана {tariffPlanId}")
    public TariffPlan getTariffPlan(String tariffPlanId) {
        String object = new Http(URL)
                .get(String.format("tariff_plans/%s?include=tariff_classes", tariffPlanId))
                .assertStatus(200)
                .toString();
        return deserialize(object);
    }

    /**
     * Отправка запроса на редактирование ТП
     *
     * @param tariffPlan {@code id} тарифный план содержащий только обновляемые поля
     * @return обновленный ТП
     */
    @Step("Редактирование тарифного плана {tariffPlan}")
    public TariffPlan editTariffPlan(TariffPlan tariffPlan) {
        String object = new Http(URL)
                .patch(String.format("tariff_plans/%s", tariffPlan.getId()), tariffPlan.toJson())
                .assertStatus(200)
                .toString();
        return deserialize(object);
    }

//    @Step("Редактирование статуса тарифного плана {tariffPlan}")
//    public String editTariffPlansStatus(TariffPlan tariffPlan) {
//        LocalDateTime.now();
//
//        JsonPath jsonPath =  jsonHelper.getJsonTemplate("/accessGroup/changeStatusTariffPlan.json")
//                .set("$.status", "planned")
//                .set("$.begin_date", "2021-09-30T18:27:00+03:00")
//                .send(URL)
//                .patch(String.format("tariff_plans/%s", tariffPlan.getId()))
//                .assertStatus(200)
//                .jsonPath();
//        return jsonPath.getString("status");
//    }

}
