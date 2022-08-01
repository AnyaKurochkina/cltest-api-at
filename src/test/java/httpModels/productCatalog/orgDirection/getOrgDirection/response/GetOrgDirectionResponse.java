package httpModels.productCatalog.orgDirection.getOrgDirection.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class GetOrgDirectionResponse implements GetImpl {

    @JsonProperty("extra_data")
    private ExtraData extraData;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("icon_url")
    private String iconUrl;

    @JsonProperty("icon_store_id")
    private String iconStoreId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("create_dt")
    private String create_dt;

    @JsonProperty("update_dt")
    private String update_dt;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getGraphVersionCalculated() {
        return null;
    }
}