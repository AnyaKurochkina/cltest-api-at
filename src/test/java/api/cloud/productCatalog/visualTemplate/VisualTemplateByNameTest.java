package api.cloud.productCatalog.visualTemplate;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.visualTeamplate.*;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.VisualTemplateSteps.*;

@Epic("Продуктовый каталог")
@Feature("Шаблоны отображения")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class VisualTemplateByNameTest extends Tests {

    CompactTemplate compactTemplate = CompactTemplate.builder()
            .name(new Name("name"))
            .type(new Type("type", "label"))
            .status(new Status("status"))
            .build();
    FullTemplate fullTemplate = FullTemplate.builder()
            .type("type")
            .value(Arrays.asList("value", "value2"))
            .build();

    @DisplayName("Проверка tag_list при копировании шаблона визуализации V2")
    @TmsLink("")
    @Test
    public void copyVisualTemplateAndCheckTagListV2Test() {
        String visualTemplateName = "clone_visual_template_and_check_tag_list_v2_test_api";
        ItemVisualTemplate visualTemplates = ItemVisualTemplate.builder()
                .name(visualTemplateName)
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .tagList(Arrays.asList("test", "api_test"))
                .build()
                .createObject();
        ItemVisualTemplate cloneTemplate = copyVisualTemplateByName(visualTemplateName);
        deleteVisualTemplateById(cloneTemplate.getId());
        assertEquals(visualTemplates.getTagList(), cloneTemplate.getTagList());
    }
}
