package api.cloud.productCatalog.action;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.action.Action;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.util.Arrays;

import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionExportTest extends Tests {
    private static Action simpleAction;
    private static Action simpleAction2;

    @BeforeAll
    public static void setUp() {
        simpleAction = createAction("export_action1_test_api");
        simpleAction2 = createAction("export_action2_test_api");
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких действий")
    @TmsLink("")
    @Test
    public void exportActionsTest() {
        ExportEntity e = new ExportEntity(simpleAction.getActionId(), simpleAction.getVersion());
        ExportEntity e2 = new ExportEntity(simpleAction2.getActionId(), simpleAction2.getVersion());
        Response response = exportObjectsById("actions", new ExportData(Arrays.asList(e, e2)).toJson());
        byte[] bytes = response.getResponse().asByteArray();
        try (FileOutputStream fos = new FileOutputStream("pathname.zip")) {
            fos.write(bytes);
            //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
        }
    }
}
