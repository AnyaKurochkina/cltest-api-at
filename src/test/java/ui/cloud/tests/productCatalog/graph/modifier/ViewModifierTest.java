package ui.cloud.tests.productCatalog.graph.modifier;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.Env;
import models.cloud.productCatalog.graph.Modification;
import models.cloud.productCatalog.graph.RootPath;
import models.cloud.productCatalog.graph.UpdateType;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.graph.GraphModifiersPage;
import ui.cloud.pages.productCatalog.graph.GraphOrderParamsPage;
import ui.cloud.pages.productCatalog.graph.GraphPage;
import ui.cloud.tests.productCatalog.graph.GraphBaseTest;

import java.util.Arrays;
import java.util.Collections;

import static steps.productCatalog.GraphSteps.partialUpdateGraphByName;

@Feature("Просмотр модификаторов")
public class ViewModifierTest extends GraphBaseTest {

    @Test
    @TmsLink("SOUL-824")
    @DisplayName("Отсутствие модификаторов на вкладке 'Параметры заказа'")
    public void viewOrderParamsSchema() {
        partialUpdateGraphByName(graph.getName(), new JSONObject().put("json_schema",
                new JSONObject().put("title", "initial_title")));
        Modification modifier1 = Modification.builder()
                .name("1")
                .envs(Collections.singletonList(Env.DEV))
                .order(1)
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(new JSONObject().put("title", "test_title_1"))
                .build();
        Modification modifier2 = Modification.builder()
                .name("2")
                .envs(Collections.singletonList(Env.DEV))
                .order(2)
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(new JSONObject().put("title", "test_title_2"))
                .build();
        partialUpdateGraphByName(graph.getName(), new JSONObject().put("modifications", Collections.singletonList(modifier1)));
        partialUpdateGraphByName(graph.getName(), new JSONObject().put("modifications", Arrays.asList(modifier1, modifier2)));
        GraphPage page = new ControlPanelIndexPage().goToGraphsPage().findAndOpenGraphPage(graph.getName());
        page.getOrderParamsTab().switchTo();
        new GraphOrderParamsPage().checkJSONSchemaContains("initial_title");
        page.goToModifiersTab();
        new GraphModifiersPage().checkModifiedJSONSchemaContains("test_title_2");
        page.getVersionSelect().set("1.0.2");
        new GraphModifiersPage().checkModifiedJSONSchemaContains("test_title_1");//BUG PO-1283 [Графы] Модификаторы
        //некорректно отображаются при смене версии
        page.getOrderParamsTab().switchTo();
        new GraphOrderParamsPage().checkJSONSchemaContains("initial_title");
    }
}
