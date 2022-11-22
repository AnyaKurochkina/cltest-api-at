package models.cloud.productCatalog.example;

import api.cloud.productCatalog.IProductCatalog;
import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import core.helper.StringUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static steps.productCatalog.ExampleSteps.*;

@Log4j2
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Example extends Entity implements IProductCatalog {
    @Builder.Default
    @JsonProperty("context_data")
    private LinkedHashMap<Object, Object> contextData = new LinkedHashMap<>();
    @Builder.Default
    @JsonProperty("form_data")
    private LinkedHashMap<Object, Object> formData = new LinkedHashMap<>();
    @Builder.Default
    @JsonProperty("json_schema")
    private LinkedHashMap<Object, Object> jsonSchema = new LinkedHashMap<>();
    @JsonProperty("update_dt")
    private String updateDt;
    private String name;
    @JsonProperty("create_dt")
    private String createDt;
    private String description;
    private String id;
    private String title;
    @Builder.Default
    @JsonProperty("ui_schema")
    private LinkedHashMap<Object, Object> uiSchema = new LinkedHashMap<>();

    @Override
    public Entity init() {
        return this;
    }

    @Override
    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/examples/createExample.json")
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.create_dt", createDt)
                .set("$.update_dt", updateDt)
                .set("$.ui_schema", uiSchema)
                .set("$.context_data", contextData)
                .set("$.form_data", formData)
                .set("$.json_schema", jsonSchema)
                .build();
    }

    @Override
    protected void create() {
        if (isExampleExists(name)) {
            deleteExampleByName(name);
        }
        Example example = createExample(toJson());
        StringUtils.copyAvailableFields(example, this);
        Assertions.assertNotNull(id, "Пример с именем: " + name + ", не создался");
    }

    @Override
    protected void delete() {
        deleteExampleById(id);
        assertFalse(isExampleExists(name));
    }
}
