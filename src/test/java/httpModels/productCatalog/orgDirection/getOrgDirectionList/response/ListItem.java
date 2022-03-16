package httpModels.productCatalog.orgDirection.getOrgDirectionList.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.ItemImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ListItem implements ItemImpl {

    @JsonProperty("extra_data")
    private ExtraData extraData;

    @JsonProperty("name")
    private String name;

    @JsonProperty("icon")
    private String icon;

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
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCreateData() {
        return create_dt;
    }

    @Override
    public String getUpDateData() {
        return update_dt;
    }
}