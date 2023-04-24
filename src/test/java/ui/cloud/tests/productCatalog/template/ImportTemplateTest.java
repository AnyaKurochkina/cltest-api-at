package ui.cloud.tests.productCatalog.template;

import core.helper.JsonHelper;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.template.Template;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import static steps.productCatalog.TemplateSteps.isTemplateExists;

@Feature("Импорт из файла")
@DisabledIfEnv("prod")
public class ImportTemplateTest extends TemplateBaseTest {

    @Test
    @DisplayName("Импорт шаблона до первого существующего объекта")
    @TmsLink("505351")
    public void importTemplateTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/templates/importTemplate.json");
        JsonPath json = new JsonPath(data);
        String name = json.get("Template.name");
        if (isTemplateExists(name)) deleteTemplate(name);
        new ControlPanelIndexPage()
                .goToTemplatesPage()
                .importTemplate("src/test/resources/json/productCatalog/templates/importTemplate.json")
                .findAndOpenTemplatePage(name)
                .checkAttributes(Template.builder()
                        .name(name)
                        .title(json.getString("Template.title"))
                        .run(json.getString("Template.run"))
                        .rollback(json.get("Template.rollback"))
                        .type(json.getString("Template.type"))
                        .description(json.getString("Template.description"))
                        .timeout(json.getInt("Template.timeout"))
                        .printedOutput(json.getList("Template.printed_output"))
                        .version("1.0.0")
                        .build())
                .deleteTemplate();
    }
}
