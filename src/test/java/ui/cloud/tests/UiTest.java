package ui.cloud.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.Windows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.Tests;
import ui.uiExtesions.ConfigExtension;

@Log4j2
@ExtendWith(CustomBeforeAllAndAfterAll.class)
@ExtendWith(ConfigExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UiTest extends Tests {
    static Windows product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();


    @BeforeAll
     void beforeAll() {
//        Selenide.open("https://cloud.vtb.ru/?context=proj-xipzuxr713&type=project&org=vtb");
//        Selenide.$x("/button[.='kek']").shouldHave(Condition.visible);
//        int f = 1/0;
        log.info("CustomBeforeAll");
    }

    @AfterAll
    void afterAll() {
//        Selenide.open("https://cloud.vtb.ru/?context=proj-xipzuxr713&type=project&org=vtb");
//        Selenide.$x("/button[.='kek']").shouldHave(Condition.visible);
//        int f = 1/0;
        log.info("CustomAfterAll");
    }

    @BeforeEach
    void setUp() {
        log.info("BeforeEach");
    }

    @AfterEach
    void tearDown() {
//        int f = 1/0;
        log.info("AfterEach");
    }

    @Test
    @Tag("test")
//    @TmsLink("867358")
    void test() {
        log.info(1);
//        new LoginPage("proj-xazpppulba").singIn();
//        open("https://ift-portal-front.apps.d0-oscp.corp.dev.vtb/container/orders/328091e3-7f99-4525-95b3-0c6b2869db6b/main?context=proj-xazpppulba&type=project&org=vtb");
//        WindowsPage page = new WindowsPage(product);
//        System.out.println(1);
    }

    @Test
    @Tag("test")
    void test2() {
//        int f = 1/0;
//        new LoginPage("proj-xazpppulba").singIn();
//        open("https://ift-portal-front.apps.d0-oscp.corp.dev.vtb/container/orders/328091e3-7f99-4525-95b3-0c6b2869db6b/main?context=proj-xazpppulba&type=project&org=vtb");
//        WindowsPage page = new WindowsPage(product);
        log.info(2);
    }


}
