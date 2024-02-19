package ui.extesions;

import api.Tests;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

@ExtendWith(ConfigExtension.class)
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ProductInjector.class)
public class UiProductTest extends Tests {
    protected String nameVM = "dasoub-apc" + UUID.randomUUID().toString().substring(3) + "lk";
}
