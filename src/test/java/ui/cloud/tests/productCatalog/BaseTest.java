package ui.cloud.tests.productCatalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.Tests;
import ui.cloud.pages.LoginPage;
import ui.uiExtesions.ConfigExtension;

@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract public class BaseTest extends Tests {

    @BeforeEach
    public void init() {
        new LoginPage().singIn();
    }
}
