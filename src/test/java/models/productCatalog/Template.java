package models.productCatalog;

import core.helper.Configure;
import core.helper.Http;
import core.helper.JsonHelper;
import httpModels.productCatalog.Template.createTemplate.response.CreateTemplateResponse;
import httpModels.productCatalog.Template.createTemplate.response.Input;
import httpModels.productCatalog.Template.createTemplate.response.Output;
import httpModels.productCatalog.Template.createTemplate.response.PrintedOutput;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.TemplateSteps;

import java.util.List;

import static core.helper.JsonHelper.convertResponseOnClass;

@Log4j2
@Builder
@Getter

public class Template extends Entity {

    private Boolean additionalInput;
    private String color;
    private String icon;
    private String description;
    private String run;
    private Boolean priorityCanBeOverridden;
    private Boolean logCanBeOverridden;
    private Integer timeout;
    private Double coordsX;
    private Output output;
    private PrintedOutput printedOutput;
    private Boolean printedOutputCanBeOverridden;
    private List<Object> restrictedPaths;
    private Integer templateId;
    private Double coordsY;
    private Object rollback;
    private List<Object> allowedPaths;
    private String logLevel;
    private List<Object> restrictedGroups;
    private Integer priority;
    private Input input;
    private Object extraData;
    private String templateName;
    private List<Object> allowedGroups;
    private Boolean additionalOutput;
    private String jsonTemplate;

    @Override
    public Entity init() {
        jsonTemplate = "productCatalog/templates/createTemplate.json";
        return this;
    }

    @Override
    public JSONObject toJson() {
        return new JsonHelper().getJsonTemplate(jsonTemplate)
                .set("$.name", templateName)
                .build();
    }

    @Override
    protected void create() {
        String response = new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .post("templates/", toJson())
                .assertStatus(201)
                .toString();
        CreateTemplateResponse createTemplateResponse = convertResponseOnClass(response, CreateTemplateResponse.class);
        templateId = createTemplateResponse.getId();
        Assertions.assertNotNull(templateId, "Шаблон с именем: " + templateName + ", не создался");
        System.out.println(templateId);
    }

    @Override
    protected void delete() {
         new Http(Configure.ProductCatalog)
                .setContentType("application/json")
                .setWithoutToken()
                .delete("templates/" + templateId + "/")
                .assertStatus(204);

        TemplateSteps templateSteps = new TemplateSteps();
        templateId = templateSteps.getTemplateIdByNameMultiSearch(templateName);
        Assertions.assertNull(templateId, String.format("Шаблон с именем: %s не удалился", templateName));
    }
}
