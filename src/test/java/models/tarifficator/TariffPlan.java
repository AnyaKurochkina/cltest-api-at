package models.tarifficator;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import core.CacheService;
import lombok.Builder;
import lombok.Data;
import org.json.JSONObject;

import java.util.List;

@Data
@Builder
public class TariffPlan {
    boolean base;
    String baseTariffPlanId;
    String beginDate;
    String createdAt;
    String endDate;
    String id;
    String oldTariffPlanId;
    String organizationName;
    String status;
    List<TariffClass> tariffClasses;
    String title;
    boolean updateOrders;
    String updatedAt;

    public JSONObject serialize() {
        return new JSONObject("{\"tariff_plan\":" + CacheService.toJson(this) + "}");
    }

    public static TariffPlan deserialize(String object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create().fromJson(object, TariffPlan.class);
    }
}
