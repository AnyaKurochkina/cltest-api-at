package api.cloud.productCatalog.jinja;

import api.Tests;
import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.Jinja2Steps.*;
import static steps.productCatalog.ProductCatalogSteps.importObjects;

@Tag("product_catalog")
@Tag("Jinja")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaImportTest extends Tests {

    @DisplayName("Импорт нескольких jinja2")
    @TmsLink("1518720")
    @Test
    public void importMultiJinjaTest() {
        String jinjaName = "import_jinja_test_api";
        if (isJinja2Exists(jinjaName)) {
            deleteJinjaByName(jinjaName);
        }
        String jinjaName2 = "import_jinja2_test_api";
        if (isJinja2Exists(jinjaName2)) {
            deleteJinjaByName(jinjaName2);
        }
        Jinja2Template jinja = createJinja(jinjaName);
        Jinja2Template jinja2 = createJinja(jinjaName2);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/jinja2/multiJinja.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/jinja2/multiJinja2.json";
        DataFileHelper.write(filePath, exportJinjaById(jinja.getId()).toString());
        DataFileHelper.write(filePath2, exportJinjaById(jinja2.getId()).toString());
        deleteJinjaByName(jinjaName);
        deleteJinjaByName(jinjaName2);
        importObjects("jinja2_templates", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isJinja2Exists(jinjaName), "Jinja2 не существует");
        assertTrue(isJinja2Exists(jinjaName2), "Jinja2 не существует");
        deleteJinjaByName(jinjaName);
        deleteJinjaByName(jinjaName2);
    }

    @DisplayName("Проверка не обновления неверсионных полей при импорте уже существующего jinja2")
    @TmsLink("SOUL-7456")
    @Test
    public void checkNotVersionedFieldsWhenImportedExistJinjaTest() {
        String description = "update description";
        String jinjaName = "check_not_versioned_fields__when_import_exist_jinja2_test_api";
        Jinja2Template jinja = createJinja(jinjaName);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/jinja2/checkNotVersionedFieldsExistJinja2Import.json";
        DataFileHelper.write(filePath, exportJinjaById(jinja.getId()).toString());
        partialUpdateJinja2(jinja.getId(), new JSONObject().put("description", description));
        importJinja2(filePath);
        DataFileHelper.delete(filePath);
        Jinja2Template jinja2ById = getJinja2ById(jinja.getId());
        assertEquals(description, jinja2ById.getDescription());
    }
}
