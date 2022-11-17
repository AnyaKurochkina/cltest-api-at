package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.visualTeamplate.CompactTemplate;
import models.cloud.productCatalog.visualTeamplate.FullTemplate;
import models.cloud.productCatalog.visualTeamplate.ItemVisualTemplate;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static steps.productCatalog.VisualTemplateSteps.deleteVisualTemplateByName;

@Epic("Конструктор.Шаблоны отображения")
@DisabledIfEnv("prod")
public class OrderTemplateBaseTest extends BaseTest {
    final static String TITLE = "AT UI Template";
    final static String DESCRIPTION = "Description";
    private static final ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/item_visual_templates/",
            "productCatalog/itemVisualTemplate/createItemVisual.json");
    private static final CompactTemplate compactTemplate = CompactTemplate.builder().name("name")
            .type("type").status("status").build();
    private static final FullTemplate fullTemplate = FullTemplate.builder().type("tab").value(Arrays.asList("testValue")).build();
    final String NAME = UUID.randomUUID().toString();
    ItemVisualTemplate orderTemplate;

    @BeforeEach
    public void setUp() {
        createOrderTemplate(NAME);
    }

    private void createOrderTemplate(String name) {
        orderTemplate = ItemVisualTemplate.builder()
                .name(name)
                .title(TITLE)
                .description(DESCRIPTION)
                .eventType(Collections.singletonList("vm"))
                .eventProvider(Collections.singletonList("vsphere"))
                .compactTemplate(compactTemplate)
                .fullTemplate(fullTemplate)
                .isActive(false)
                .build()
                .createObject();
    }

    void deleteOrderTemplate(String name) {
        deleteVisualTemplateByName(name);
    }
}
