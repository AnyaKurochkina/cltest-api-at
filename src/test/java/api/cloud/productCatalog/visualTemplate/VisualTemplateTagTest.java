package api.cloud.productCatalog.visualTemplate;

import api.Tests;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.visualTeamplate.*;
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
import static steps.productCatalog.VisualTemplateSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Шаблоны отображения")
@DisabledIfEnv("prod")
public class VisualTemplateTagTest extends Tests {

    @DisplayName("Добавление/Удаление списка Тегов в шаблонах визуализации")
    @TmsLinks({@TmsLink("1710024"), @TmsLink("1710026")})
    @Test
    public void addTagVisualTemplateTest() {
        List<String> tagList = Arrays.asList("test_api", "test_api2");
        ItemVisualTemplate visualTemplate = createVisualTemplate("add_tag1_test_api");
        ItemVisualTemplate visualTemplate2 = createVisualTemplate("add_tag2_test_api");
        addTagListToVisualTemplate(tagList, visualTemplate.getName(), visualTemplate2.getName());
        assertEquals(tagList, getVisualTemplateById(visualTemplate.getId()).getTagList());
        assertEquals(tagList, getVisualTemplateById(visualTemplate2.getId()).getTagList());
        removeTagListToVisualTemplate(tagList, visualTemplate.getName(), visualTemplate2.getName());
        assertTrue(getVisualTemplateById(visualTemplate.getId()).getTagList().isEmpty());
        assertTrue(getVisualTemplateById(visualTemplate2.getId()).getTagList().isEmpty());
    }

    @DisplayName("Проверка значения поля tag_list в шаблонах визуализации")
    @TmsLink("1710039")
    @Test
    public void checkVisualTemplateTagListValueTest() {
        List<String> tagList = Arrays.asList("visual_template_tag_test_value", "visual_template_tag_test_value2");
        ItemVisualTemplate visualTemplate = ItemVisualTemplate.builder()
                .name("visual_template_check_tag_list_value_test_api")
                .tagList(tagList)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(CompactTemplate.builder()
                        .name(new Name("name"))
                        .type(new Type("type", "label"))
                        .status(new Status("status"))
                        .build())
                .fullTemplate(FullTemplate.builder()
                        .type("type")
                        .value(Arrays.asList("value", "value2"))
                        .build())
                .isActive(false)
                .build()
                .createObject();
        List<String> visualTemplateTagList = visualTemplate.getTagList();
        assertTrue(tagList.size() == visualTemplateTagList.size() && tagList.containsAll(visualTemplateTagList) && visualTemplateTagList.containsAll(tagList));
        tagList = Collections.singletonList("visual_template_tag_test_value3");
        partialUpdateVisualTemplate(visualTemplate.getId(), new JSONObject().put("tag_list", tagList));
        ItemVisualTemplate createdVisualTemplate = getVisualTemplateById(visualTemplate.getId());
        AssertUtils.assertEqualsList(tagList, createdVisualTemplate.getTagList());
    }
}

