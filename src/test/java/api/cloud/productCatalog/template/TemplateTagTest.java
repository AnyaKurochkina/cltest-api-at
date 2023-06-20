package api.cloud.productCatalog.template;

import api.Tests;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.TemplateSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Шаблоны")
@DisabledIfEnv("prod")
public class TemplateTagTest extends Tests {

    @DisplayName("Добавление/Удаление списка Тегов в шаблонах")
    @TmsLinks({@TmsLink("1709863"), @TmsLink("1709866")})
    @Test
    public void addTagTemplateTest() {
        List<String> tagList = Arrays.asList("test_api", "test_api2");
        Template template1 = createTemplateByName("add_tag1_test_api");
        Template template2 = createTemplateByName("add_tag2_test_api");
        addTagListToTemplate(tagList, template1.getName(), template2.getName());
        assertEquals(tagList, getTemplateById(template1.getId()).getTagList());
        assertEquals(tagList, getTemplateById(template2.getId()).getTagList());
        removeTagListToTemplate(tagList, template1.getName(), template2.getName());
        assertTrue(getTemplateById(template1.getId()).getTagList().isEmpty());
        assertTrue(getTemplateById(template2.getId()).getTagList().isEmpty());
    }

    @DisplayName("Проверка значения поля tag_list в шаблонах")
    @TmsLink("1709868")
    @Test
    public void checkTemplateTagListValueTest() {
        List<String> tagList = Arrays.asList("template_tag_test_value", "template_tag_test_value2");
        Template template = Template.builder()
                .name("at_api_check_tag_list_value")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(tagList)
                .build()
                .createObject();
        List<String> templateTagList = template.getTagList();
        assertTrue(tagList.size() == templateTagList.size() && tagList.containsAll(templateTagList) && templateTagList.containsAll(tagList));
        tagList = Collections.singletonList("template_tag_test_value3");
        partialUpdateTemplate(template.getId(), new JSONObject().put("tag_list", tagList));
        Template createdTemplate = getTemplateById(template.getId());
        AssertUtils.assertEqualsList(tagList, createdTemplate.getTagList());
    }

    @DisplayName("Проверка не версионности поля tag_list в шаблонах")
    @TmsLink("1709881")
    @Test
    public void checkTemplateTagListVersioning() {
        List<String> tagList = Arrays.asList("test_api", "test_api2");
        Template template = Template.builder()
                .name("at_api_template_check_tag_list_versioning")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(tagList)
                .build()
                .createObject();
        tagList = Collections.singletonList("test_api3");
        partialUpdateTemplate(template.getId(), new JSONObject().put("tag_list", tagList));
        Template updatedTemplate = getTemplateById(template.getId());
        assertEquals("1.0.0", updatedTemplate.getVersion());
    }
}

