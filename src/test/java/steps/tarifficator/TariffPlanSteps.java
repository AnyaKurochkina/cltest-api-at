package steps.tarifficator;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.tarifficator.TariffPlan;
import steps.Steps;

import java.util.ArrayList;
import java.util.List;

import static core.helper.Configure.TarifficatorURL;

@Log4j2
public class TariffPlanSteps extends Steps {

    @SneakyThrows
    public static TariffPlan deserialize(String object) {
        return JsonHelper.getCustomObjectMapper().readValue(object, TariffPlan.class);
    }

    /**
     * Отправка запроса на создание ТП
     *
     * @param tariffPlan объект класса {@code TariffPlan}, где заполнены поля {@code oldTariffPlanId, title, base}
     * @return созданный ТП
     */
    @Step("Создание тарифного плана {tariffPlan}")
    public static TariffPlan createTariffPlan(TariffPlan tariffPlan) {
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
    public static List<TariffPlan> getTariffPlanList(String urlParameters) {
        ObjectMapper objectMapper = JsonHelper.getCustomObjectMapper();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, TariffPlan.class);
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
        return objectMapper.convertValue(allResponseList, type);
    }

    /**
     * Отправка запроса на получение ТП
     *
     * @param tariffPlanId {@code id} тарифного плана
     * @return запрашиваемый ТП
     */
    @Step("Получение тарифного плана {tariffPlanId}")
    public static TariffPlan getTariffPlan(String tariffPlanId) {
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
    public static TariffPlan editTariffPlan(TariffPlan tariffPlan) {
        String object = new Http(TarifficatorURL)
                .body(tariffPlan.toJson())
                .patch("tariff_plans/{}", tariffPlan.getId())
                .assertStatus(200)
                .toString();
        tariffPlan.save();
        return deserialize(object);
    }

}
