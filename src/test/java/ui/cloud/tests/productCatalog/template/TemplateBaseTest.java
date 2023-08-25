package ui.cloud.tests.productCatalog.template;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.template.Template;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ui.cloud.tests.productCatalog.ProductCatalogUITest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static steps.productCatalog.TemplateSteps.deleteTemplateById;
import static steps.productCatalog.TemplateSteps.getTemplateByName;

@Epic("Конструктор.Шаблоны узлов")
@DisabledIfEnv("prod")
public class TemplateBaseTest extends ProductCatalogUITest {

    protected final static String TITLE = "AT UI Template";
    protected final static String DESCRIPTION = "Description";
    protected final static String TYPE = "system_nodes";
    protected final static String QUEUE_NAME = "internal";
    protected final String NAME = UUID.randomUUID().toString();
    protected Template template;

    @BeforeEach
    public void setUp() {
        createTemplate(NAME);
    }

    @AfterEach
    public void tearDown() {
        deleteTemplate(NAME);
    }

    private void createTemplate(String name) {
        Map<String, String> value = new HashMap<>();
        Map<String, Map<String, String>> input = new HashMap<>();
        Map<String, Map<String, String>> output = new HashMap<>();
        input.put("input_param", value);
        output.put("output_param", value);
        template = Template.builder()
                .name(name)
                .title(TITLE)
                .description(DESCRIPTION)
                .type(TYPE)
                .run(QUEUE_NAME)
                .rollback("")
                .input(input)
                .output(output)
                .printedOutput(Collections.singletonList(new HashMap<String, String>() {{
                    put("type", "text");
                }}))
                .timeout(100)
                .build()
                .createObject();
    }

    void deleteTemplate(String name) {
        deleteTemplateById(getTemplateByName(name).getId());
    }
}
