package ui.cloud.tests.productCatalog.template;

import httpModels.productCatalog.template.getListTemplate.response.GetTemplateListResponse;
import models.productCatalog.Template;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.uiModels.Node;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@DisabledIfEnv("prod")
public class TemplateBaseTest extends BaseTest {

    protected final static String TITLE = "AT UI Template";
    protected final static String DESCRIPTION = "Description";
    protected final static String TYPE = "system_nodes";
    protected final static String QUEUE_NAME = "internal";
    protected final String NAME = UUID.randomUUID().toString();
    protected Template template;
    protected ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/templates/",
            "productCatalog/templates/createTemplate.json");

    @BeforeEach
    @DisplayName("Создание графов через API")
    public void setUp() {
        createTemplate(NAME);
    }

    @AfterEach
    @DisplayName("Удаление графов, созданных в сетапе")
    public void tearDown() {
        deleteTemplate(NAME);
    }

    public void createTemplate(String name) {
        Map<String, String> value = new LinkedHashMap<>();
        Map<String, Map<String, String>> input = new LinkedHashMap<>();
        Map<String, Map<String, String>> output = new LinkedHashMap<>();
        input.put(new Node().getInputKey(), value);
        output.put(new Node().getOutputKey(), value);
        template = Template.builder()
                .templateName(name)
                .title(TITLE)
                .description(DESCRIPTION)
                .type(TYPE)
                .run(QUEUE_NAME)
                .rollback("")
                .input(input)
                .output(output)
                .timeout(100)
                .build()
                .createObject();
    }

    public void deleteTemplate(String name) {
        ProductCatalogSteps steps = new ProductCatalogSteps(Template.productName);
        steps.getDeleteObjectResponse(steps
                .getProductObjectIdByNameWithMultiSearch(name, GetTemplateListResponse.class)).assertStatus(204);
    }
}
