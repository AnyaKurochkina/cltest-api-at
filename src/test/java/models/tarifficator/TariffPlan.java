package models.tarifficator;

import core.helper.Configure;
import core.helper.http.Http;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import lombok.*;
import models.Entity;
import models.authorizer.Organization;
import org.json.JSONObject;
import steps.tarifficator.TariffPlanSteps;

import java.util.*;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
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

    @Builder.Default
    transient TariffPlanSteps tariffPlanSteps = new TariffPlanSteps();


    public JSONObject toJson() {
        return new JSONObject("{\"tariff_plan\":" + JsonHelper.toJson(this) + "}");
    }

    @Override
    protected void delete() {}

    @Override
    public Entity init() {
        if(title == null)
            title = "AT " + new Date();
        if(base == null)
            base = true;
        if(oldTariffPlanId == null) {
            TariffPlan activeTariff = tariffPlanSteps.getTariffPlanList("f[base]=true&f[status][]=active").get(0);
            oldTariffPlanId = activeTariff.getId();
        }
        if(!base && organizationName == null) {
            organizationName = ((Organization) Organization.builder().build().createObject()).getName();
        }
        return this;
    }

    @Override
    @Step("Создание тарифного плана")
    protected void create() {
        String object = new Http(Configure.TarifficatorURL)
                .body(toJson())
                .post("tariff_plans")
                .assertStatus(201)
                .toString();
        StringUtils.copyAvailableFields(tariffPlanSteps.deserialize(object), this);
    }



}
