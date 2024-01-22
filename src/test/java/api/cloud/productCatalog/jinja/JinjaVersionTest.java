package api.cloud.productCatalog.jinja;

import core.helper.StringUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.Jinja2Steps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaVersionTest {

    @DisplayName("Проверка версионности поля jinja2_template")
    @TmsLink("SOUL-6773")
    @Test
    public void checkVersionWhenJinjaTemplateUpdateTest() {
        String jinjaName = "check_version_when_jinja_template_update_test_api";
        Jinja2Template jinja2 = createJinja(jinjaName);
        String jinjaTemplate = "test";
        partialUpdateJinja2(jinja2.getId(), new JSONObject().put("jinja2_template", jinjaTemplate)).assertStatus(200);
        Jinja2Template jinja2ById = getJinja2ById(jinja2.getId());
        assertEquals("1.0.1", jinja2ById.getVersion());
    }

    @DisplayName("Проверка версионности поля jinja2_data")
    @TmsLink("SOUL-6774")
    @Test
    public void checkVersionWhenJinjaDataUpdateTest() {
        String jinjaName = "check_version_when_jinja_data_update_test_api";
        Jinja2Template jinja2 = createJinja(jinjaName);
        JSONObject jinjaData = new JSONObject().put("test", "test_api");
        partialUpdateJinja2(jinja2.getId(), new JSONObject()
                .put("jinja2_data", jinjaData)).assertStatus(200);
        Jinja2Template jinja2ById = getJinja2ById(jinja2.getId());
        assertEquals("1.0.1", jinja2ById.getVersion());
    }

    @DisplayName("Обновление jinja с указанием версии в граничных значениях")
    @TmsLink("SOUL-6775")
    @Test
    public void updateJinjaAndGetVersion() {
        Jinja2Template jinja2 = Jinja2Template.builder()
                .name("jinja2_version_test_api")
                .version("1.0.999")
                .build()
                .createObject();
        partialUpdateJinja2(jinja2.getId(), new JSONObject().put("jinja2_template", "test"));
        String currentVersion = getJinja2ById(jinja2.getId()).getVersion();
        assertEquals("1.1.0", currentVersion);
        partialUpdateJinja2(jinja2.getId(), new JSONObject()
                .put("jinja2_template", "test2")
                .put("version", "1.999.999"));
        partialUpdateJinja2(jinja2.getId(), new JSONObject()
                .put("jinja2_template", "test3"));
        currentVersion = getJinja2ById(jinja2.getId()).getVersion();
        assertEquals("2.0.0", currentVersion);
        partialUpdateJinja2(jinja2.getId(), new JSONObject()
                .put("jinja2_template", "test4")
                .put("version", "999.999.999"));
        String message = partialUpdateJinja2(jinja2.getId(), new JSONObject()
                .put("jinja2_template", "test5"))
                .assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("Version counter full [999, 999, 999]", message);
    }

    @DisplayName("Проверка неверсионных полей jinja2")
    @TmsLink("SOUL-6776")
    @Test
    public void updateNotVersionFieldsJinjaByIdAndCheckVersion() {
        String updateName = "check_not_version_jinja2_fields_update_name_test_api";
        String updateTitle = StringUtils.getRandomStringApi(6);
        String updateDescription = StringUtils.getRandomStringApi(6);
        if (isJinja2Exists(updateName)) {
            deleteJinjaByName(updateName);
        }
        Jinja2Template jinjaObject = createJinja("test_object_not_versioned_fields_test_api");
        jinjaObject.setName(updateName);
        jinjaObject.setTitle(updateTitle);
        jinjaObject.setDescription(updateDescription);
        jinjaObject.setVersion(null);
        putJinja2ById(jinjaObject.getId(), jinjaObject
                .toJson());
        Jinja2Template updatedJinja = getJinja2ById(jinjaObject.getId());
        assertEquals("1.0.0", updatedJinja.getVersion());
    }
}
