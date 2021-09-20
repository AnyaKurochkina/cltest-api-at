package models.tarifficator;

import core.CacheService;
import lombok.*;
import models.Entity;
import org.json.JSONObject;
import steps.tarifficator.TariffPlanSteps;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
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
    @EqualsAndHashCode.Include
    String title;
    Boolean updateOrders;
    Date updatedAt;

    @Builder.Default
    transient TariffPlanSteps tariffPlanSteps = new TariffPlanSteps();


    public JSONObject serialize() {
        return new JSONObject("{\"tariff_plan\":" + CacheService.toJson(this) + "}");
    }

    public static TariffPlan deserialize(String object) {
        return CacheService.getCustomGson().fromJson(object, TariffPlan.class);
    }

    @Override
    public Entity create() {
        if(title == null)
            title = "AT " + new Date();
        if(base == null)
            base = true;
        if(oldTariffPlanId == null) {
            TariffPlan activeTariff = tariffPlanSteps.getTariffPlanList("f[base]=true&f[status][]=active").get(0);
            oldTariffPlanId = activeTariff.getId();
        }
        return tariffPlanSteps.createTariffPlan(this);
    }

    @Override
    public void delete() {}

//    @Override
//    public void reset() {
//
//    }
//
//    @Override
//    public Map<Integer, Integer> graphState() {
//        return new HashMap<Integer, Integer>() {{
//            put(TariffPlanStatus.Num.draft, TariffPlanStatus.Num.planned);
//            put(TariffPlanStatus.Num.planned, TariffPlanStatus.Num.draft);
//            put(TariffPlanStatus.Num.planned, TariffPlanStatus.Num.active);
//            put(TariffPlanStatus.Num.active, TariffPlanStatus.Num.archived);
//        }};
//    }


}
