package ui.cloud.tests.productCatalog;

import api.Tests;
import core.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.ControlPanelLoginPage;
import ui.extesions.ConfigExtension;

@ExtendWith(ConfigExtension.class)
abstract public class ProductCatalogUITest extends Tests {

    @BeforeEach
    public void init() {
        new ControlPanelLoginPage().signIn(Role.PRODUCT_CATALOG_ADMIN);
    }
}
