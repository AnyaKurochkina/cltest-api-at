package ui.cloud.tests.productCatalog.template;

import core.helper.JsonHelper;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.models.Template;

@Feature("Импорт из файла")
@DisabledIfEnv("prod")
public class ImportTemplateTest extends BaseTest {

    @Test
    @DisplayName("Импорт шаблона до первого существующего объекта")
    @TmsLink("505351")
    public void importTemplateTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/templates/importTemplate.json");
        JsonPath json = new JsonPath(data);
        String name = json.get("Template.name");
        new IndexPage()
                .goToTemplatesPage()
                .importTemplate("src/test/resources/json/productCatalog/templates/importTemplate.json")
                .findAndOpenTemplatePage(name)
                .checkTemplateAttributes(new Template(name, json.get("Template.title"),
                        json.get("Template.run"), json.get("Template.rollback"), json.get("Template.type")))
                .deleteTemplate();
    }
}
