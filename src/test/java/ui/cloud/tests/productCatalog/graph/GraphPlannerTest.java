package ui.cloud.tests.productCatalog.graph;

import core.utils.Waiting;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.ContextSettingsPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.graph.GraphPage;
import ui.elements.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Feature("Конфигурации графа для планировщика")
public class GraphPlannerTest extends GraphBaseTest {

    String graphName = "graph_for_at_ui_product";
    String productName = "at_ui_product";
    Project project = Project.builder().isForOrders(true).projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
            .build().createObject();

    @BeforeEach
    public void setup() {
        new ControlPanelIndexPage().goToContextSettingsPage().getDevProjectInput().setValue(project.getId());
        new ContextSettingsPage().save();
    }

    @Test
    @Disabled("TODO исправить")
    @TmsLinks({@TmsLink("SOUL-833"), @TmsLink("SOUL-835")})
    @DisplayName("Добавить и удалить конфигурацию для планировщика")
    @EnabledIfEnv("ift")
    public void addAndDeletePlannerOption() {
        GraphPage page = new ControlPanelIndexPage().goToGraphsPage().findAndOpenGraphPage(graphName);
        page.getOrderParamsTab().switchTo();
        Select.byLabel("Сетевой сегмент").set("dev-srv-app");
        page.getPlannerTab().click();
        page.getAddOptionButton().click();
        Select.byLabel("Продукт").setContains(productName);
        String optionName = "QA AT Option_" + RandomStringUtils.randomAlphanumeric(6);
        Waiting.sleep(1000); //Баг, что не ожидается загрузка product_title
        Input.byName("option").setValue(optionName);
        page.getSaveOptionButton().click();
        Alert.green("Конфигурация {} успешно добавлена", optionName);
        Table table = new Table("Название конфигурации");
        assertTrue(table.isColumnValueEquals("Название конфигурации", optionName));
        Table.Row row = table.getRowByColumnValue("Название конфигурации", optionName);
        assertTrue(row.getValueByColumn("Продукт").contains(productName));
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        assertEquals(row.getValueByColumn("Дата создания"), currentDate);
        assertEquals(row.getValueByColumn("Дата изменения"), currentDate);
        new Menu(row.get().$x(".//button")).select("Редактировать");
        Button.byText("Обновить").click();
        new Menu(row.get().$x(".//button")).select("Удалить");
        new DeleteDialog("Удаление конфигурации")
                .checkText(format("Вы действительно хотите удалить конфигурацию \"{}\"?", optionName))
                .submitAndDelete();
        Alert.green("Конфигурация успешно удалена");
    }
}
