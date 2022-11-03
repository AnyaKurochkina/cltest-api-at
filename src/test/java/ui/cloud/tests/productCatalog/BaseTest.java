package ui.cloud.tests.productCatalog;

import core.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import api.Tests;
import ui.cloud.pages.LoginPageControlPanel;
import ui.extesions.ConfigExtension;

@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract public class BaseTest extends Tests {

    @BeforeEach
    public void init() {
        new LoginPageControlPanel().signIn(Role.PRODUCT_CATALOG_ADMIN);
    }
}
