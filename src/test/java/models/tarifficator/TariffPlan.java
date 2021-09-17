package models.tarifficator;

import core.CacheService;
import lombok.*;
import models.EntityOld;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class TariffPlan extends EntityOld {
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

    public JSONObject serialize() {
        return new JSONObject("{\"tariff_plan\":" + CacheService.toJson(this) + "}");
    }

    public static TariffPlan deserialize(String object) {
        return CacheService.getCustomGson().fromJson(object, TariffPlan.class);
    }
}
