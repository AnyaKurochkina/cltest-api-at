package ui.cloud.tests;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.TmsLink;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import models.orderService.products.Windows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import tests.Tests;
import ui.elements.Graph;
import ui.elements.Table;
import ui.uiExtesions.ConfigExtension;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.executeJavaScript;

@Log4j2
//@ExtendWith(CustomBeforeAllAndAfterAll.class)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UiTest extends Tests {
    static Windows product = Windows.builder().env("DEV").platform("OpenStack").segment("dev-srv-app").build();


    @Data
    @AllArgsConstructor
    class Node {
        int x, y, w, h;
        Color c;
    }


    @Test
    void name() {
//        Selenide.open("https://prod-portal-front.cloud.vtb.ru/vm/orders/761a5b34-ecfb-4033-ab66-a2a65cf205ec/main?context=proj-ln4zg69jek&type=project&org=vtb");
        Selenide.open("https://ift-portal-front.apps.d0-oscp.corp.dev.vtb/compute/orders/910b162c-79bb-42dd-97fe-560fa0fc3149/history?context=proj-xazpppulba&type=project&org=vtb");
//        Table.getTableByColumnName("Дата запуска");

        System.out.println(1);

//        Selenide.$x("//canvas");
//        new Graph().getNodeCoordinates();


        System.out.println(1);

    }


//    boolean isIntersect(Node box1, Node box2) {
//        if (box1.getY() < box2.getY() + box2.getH() || box1.getY() + box1.getH() > box2.getY())
//            return false;
//        return box1.getX() + box1.getW() >= box2.getX() && box1.getX() <= box2.getX() + box2.getW();
//    }


    //    @BeforeAll
    void beforeAll() {
//        Selenide.open("https://cloud.vtb.ru/?context=proj-xipzuxr713&type=project&org=vtb");
//        Selenide.$x("/button[.='kek']").shouldHave(Condition.visible);
//        int f = 1/0;
        log.info("CustomBeforeAll");
    }

    //    @AfterAll
    void afterAll() {
//        Selenide.open("https://cloud.vtb.ru/?context=proj-xipzuxr713&type=project&org=vtb");
//        Selenide.$x("/button[.='kek']").shouldHave(Condition.visible);
//        int f = 1/0;
        log.info("CustomAfterAll");

    }

    @BeforeEach
    void setUp() {
//        int f = 1/0;
        log.info("BeforeEach");
    }

    @AfterEach
    void tearDown() {
//        int f = 1/0;
        log.info("AfterEach");
    }

    @Test
//    @Tag("test")
    @Order(1)
    @TmsLink("867358")
    void test() {
//        int f = 1/0;
        log.info("test1()");
    }

    @Test
    @Order(2)
    @Tag("test")
    void test2() {
//        int f = 1/0;
        log.info("test2()");
    }

    @Test
    @Order(3)
    @Tag("test")
    void test3() {
//        int f = 1/0;
        log.info("test3()");
    }

    @Test
    @Order(4)
//    @Tag("test")
    void test4() {
//        int f = 1/0;
        log.info("test4()");
    }


}
