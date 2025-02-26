package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.visualTeamplate.*;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import ui.cloud.tests.productCatalog.ProductCatalogUITest;

import java.util.Collections;
import java.util.UUID;

import static steps.productCatalog.VisualTemplateSteps.deleteVisualTemplateByName;

@Epic("Конструктор.Шаблоны отображения")
@DisabledIfEnv("prod")
public class OrderTemplateBaseTest extends ProductCatalogUITest {
    final static String TITLE = "AT UI Template";
    final static String DESCRIPTION = "Description";
    private static final CompactTemplate compactTemplate = CompactTemplate.builder()
            .name(new Name("name"))
            .type(new Type("type"))
            .status(new Status("status"))
            .build();
    private static final FullTemplate fullTemplate = FullTemplate.builder().type("tab").value(Collections.singletonList("testValue")).build();
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
