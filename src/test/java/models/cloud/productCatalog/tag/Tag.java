package models.cloud.productCatalog.tag;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import core.helper.http.Response;
import io.qameta.allure.Step;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.productCatalog.TagSteps.*;
import static steps.productCatalog.TemplateSteps.isTemplateExists;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)

public class Tag extends Entity implements IProductCatalog {

    private Integer id;
    private String name;
    @JsonProperty("create_dt")
    private String createDt;

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/tag/createTag.json")
                .set("$.name", name)
                .build();
    }

    @Override
    @Step("Создание шаблона")
    protected void create() {
        if (isTagExists(name)) {
            Tag template = getTagByName(name);
            Response response = getTagUsedObjectsByName(name);
            deleteTagByName(name);
        }
        Tag createTemplate = createTagByName(name);
        StringUtils.copyAvailableFields(createTemplate, this);
        Assertions.assertNotNull(id, "Тег с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        deleteTagByName(name);
        assertFalse(isTemplateExists(name));
    }
}
