package steps.tarifficator;

import com.google.gson.reflect.TypeToken;
import core.helper.Http;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import models.tarifficator.TariffPlan;
import org.json.JSONArray;
import steps.Steps;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static core.helper.Configure.TarifficatorURL;

@Log4j2
public class TariffPlanSteps extends Steps {

    public TariffPlan deserialize(String object) {
        return JsonHelper.getCustomGson().fromJson(object, TariffPlan.class);
    }

    /**
     * Отправка запроса на создание ТП
     *
     * @param tariffPlan объект класса {@code TariffPlan}, где заполнены поля {@code oldTariffPlanId, title, base}
     * @return созданный ТП
     */
    @Step("Создание тарифного плана {tariffPlan}")
    public TariffPlan createTariffPlan(TariffPlan tariffPlan) {
        String object = new Http(TarifficatorURL)
                .body(tariffPlan.toJson())
                .post("tariff_plans")
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
            responseList = new Http(TarifficatorURL)
                    .get("tariff_plans?page={}&per_page=100&{}", i, urlParameters)
                    .assertStatus(200)
                    .jsonPath()
                    .getList("list");
            allResponseList.addAll(responseList);
            i++;
        } while (responseList.size() > 0);
        return JsonHelper.getCustomGson().fromJson(new JSONArray(allResponseList).toString(), type);
    }

    /**
     * Отправка запроса на получение ТП
     *
     * @param tariffPlanId {@code id} тарифного плана
     * @return запрашиваемый ТП
     */
    @Step("Получение тарифного плана {tariffPlanId}")
    public TariffPlan getTariffPlan(String tariffPlanId) {
        String object = new Http(TarifficatorURL)
                .get("tariff_plans/{}?include=tariff_classes", tariffPlanId)
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
        String object = new Http(TarifficatorURL)
                .body(tariffPlan.toJson())
                .patch("tariff_plans/{}", tariffPlan.getId())
                .assertStatus(200)
                .toString();
        tariffPlan.save();
        return deserialize(object);
    }

}
