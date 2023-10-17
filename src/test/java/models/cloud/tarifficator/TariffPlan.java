package models.cloud.tarifficator;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.enums.Role;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import models.Entity;
import models.cloud.authorizer.Organization;
import org.json.JSONObject;
import steps.tarifficator.TariffPlanSteps;

import java.util.Date;
import java.util.List;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class TariffPlan extends Entity {
    Boolean base;
    String baseTariffPlanId;
    Date beginDate;
    Date createdAt;
    Date endDate;
    @EqualsAndHashCode.Include
    String id;
    String oldTariffPlanId;
    String organizationName;
    TariffPlanStatus status;
    List<TariffClass> tariffClasses;
    List<TariffPlanServices> tariffPlanServices;
    @EqualsAndHashCode.Include
    @ToString.Include
    String title;
    Boolean updateOrders;
    Date updatedAt;
    Boolean activationErrorsCount;

    @JsonProperty("default")
    Boolean defaultField;
    List<String> organizationNames;


    @SneakyThrows
    public JSONObject toJson() {
        return new JSONObject("{\"tariff_plan\":" + JsonHelper.getCustomObjectMapper().writeValueAsString(this) + "}");
    }

    @Override
    protected void delete() {
    }

    @Override
    public Entity init() {
        if (title == null)
            title = "AT " + new java.util.Date();
        if (base == null)
            base = true;
        if (oldTariffPlanId == null) {
            TariffPlan activeTariff = TariffPlanSteps.getTariffPlanList("f[base]=true&f[status][]=active").get(0);
            oldTariffPlanId = activeTariff.getId();
        }
        if (!base && organizationName == null) {
            organizationName = ((Organization) Organization.builder().type("default").build().createObject()).getName();
        }
        return this;
    }

    @Override
    @Step("Создание тарифного плана")
    protected void create() {
        String id = new Http(Configure.TarifficatorURL)
                .setRole(Role.TARIFFICATOR_ADMIN)
                .body(toJson())
                .post("/v1/tariff_plans")
                .assertStatus(201)
                .jsonPath()
                .getString("id");
        TariffPlan newTariffPlan = TariffPlanSteps.getTariffPlan(id);
        StringUtils.copyAvailableFields(newTariffPlan, this);
    }


}
