package models.cloud.productCatalog.productCard;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import static steps.productCatalog.ProductCardSteps.createProductCard;
import static steps.productCatalog.ProductCardSteps.deleteProductCard;

@Log4j2
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"createDt", "updateDt"})
@ToString
@Getter
public class ProductCard extends Entity {
    @JsonProperty("card_items")
    private List<CardItems> cardItems;
    private String name;
    private String title;
    private String description;
    private Integer number;
    private String id;
    @JsonProperty("tag_list")
    private List<String> tagList;
    @JsonProperty("create_dt")
    private Date createDt;
    @JsonProperty("update_dt")
    private Date updateDt;

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/productCard/createProductCard.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.number", number)
                .set("$.description", description)
                .setIfNullRemove("$.cardItems", cardItems)
                .setIfNullRemove("$.tag_list", tagList)
                .build();
    }

    @Override
    protected void create() {
        id = createProductCard(toJson()).getId();
    }

    @Override
    protected void delete() {
        deleteProductCard(id);
    }
}
