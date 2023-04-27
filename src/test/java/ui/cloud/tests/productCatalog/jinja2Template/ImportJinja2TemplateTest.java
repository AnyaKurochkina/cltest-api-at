package ui.cloud.tests.productCatalog.jinja2Template;

import core.helper.JsonHelper;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import static steps.productCatalog.Jinja2Steps.deleteJinjaByName;
import static steps.productCatalog.Jinja2Steps.isJinja2Exists;

@Feature("Импорт из файла шаблона Jinja2")
public class ImportJinja2TemplateTest extends Jinja2TemplateBaseTest {

    @Override
    @BeforeEach
    public void setUp() {
    }

    @Test
    @TmsLink("836758")
    @DisplayName("Импорт шаблона Jinja2 из файла")
    public void importAllowedAction() {
        String data = JsonHelper.getStringFromFile("/productCatalog/jinja2/importJinja2Template.json");
        JsonPath json = new JsonPath(data);
        String name = json.getString("Jinja2Template.name");
        if (isJinja2Exists(name)) deleteJinjaByName(name);
        new ControlPanelIndexPage()
                .goToJinja2TemplatesListPage()
                .importJinja2Template("src/test/resources/json/productCatalog/jinja2/importJinja2Template.json")
                .findAndOpenJinja2TemplatePage(name)
                .checkAttributes(Jinja2Template.builder()
                        .name(name)
                        .title(json.getString("Jinja2Template.title"))
                        .description(json.getString("Jinja2Template.description"))
                        .jinja2Template(json.getString("Jinja2Template.jinja2_template"))
                        .jinja2Data(json.get("Jinja2Template.jinja2_data"))
                        .build());
    }
}
