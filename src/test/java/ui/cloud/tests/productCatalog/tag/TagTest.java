package ui.cloud.tests.productCatalog.tag;

import org.junit.jupiter.api.AfterAll;
import ui.cloud.tests.productCatalog.ProductCatalogUITest;

import java.util.ArrayList;
import java.util.List;

import static steps.productCatalog.TagSteps.deleteTagByName;

public class TagTest extends ProductCatalogUITest {
    protected static final List<String> tagList = new ArrayList<>();

    @AfterAll
    public static void deleteTags() {
        for (String name : tagList) {
            deleteTagByName(name);
        }
    }
}
