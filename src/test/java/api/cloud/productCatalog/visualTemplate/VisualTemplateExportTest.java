package api.cloud.productCatalog.visualTemplate;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.visualTeamplate.*;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ProductCatalogSteps.exportObjectByIdWithTags;
import static steps.productCatalog.VisualTemplateSteps.*;

@Epic("Продуктовый каталог")
@Feature("Шаблоны отображения")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class VisualTemplateExportTest extends Tests {
    CompactTemplate compactTemplate = CompactTemplate.builder()
            .name(new Name("name"))
            .type(new Type("type", "label"))
            .status(new Status("status"))
            .build();
    FullTemplate fullTemplate = FullTemplate.builder()
            .type("type")
            .value(Arrays.asList("value", "value2"))
            .build();

    @DisplayName("Экспорт шаблона визуализации по Id с tag_list")
    @TmsLink("SOUL-7111")
    @Test
    public void exportVisualTemplateByIdWithTagListTest() {
        String visualTemplateName = "visual_template_export_with_tag_list_test_api";
        List<String> expectedTagList = Arrays.asList("export_test", "test2");
        if (isVisualTemplateExists(visualTemplateName)) {
            deleteVisualTemplateByName(visualTemplateName);
        }
        JSONObject json = ItemVisualTemplate.builder()
                .name(visualTemplateName)
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .tagList(expectedTagList)
                .build()
                .toJson();
        ItemVisualTemplate itemVisualTemplate = createVisualTemplate(json).assertStatus(201).extractAs(ItemVisualTemplate.class);
        List<String> actualTagList = exportObjectByIdWithTags("item_visual_templates", itemVisualTemplate.getId())
                .jsonPath().getList("ItemVisualisationTemplate.tag_name_list");
        assertEquals(actualTagList, expectedTagList);
        deleteVisualTemplateByName(visualTemplateName);
    }

}
