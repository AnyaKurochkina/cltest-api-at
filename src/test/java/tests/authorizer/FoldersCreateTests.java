package tests.authorizer;

import io.qameta.allure.*;
import models.authorizer.Folder;
import models.authorizer.Organization;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import steps.authorizer.AuthorizerSteps;
import tests.Tests;

@Epic("Организационная структура")
@Feature("Папки")
@Tags({@Tag("regress"), @Tag("orgStructure3"), @Tag("smoke")})
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FoldersCreateTests extends Tests {
//    AuthorizerSteps authorizerSteps = new AuthorizerSteps();

    @Test
//    @Order(1)
    @Story("Создание бизнес-блока")
    public void createBusinessBlock() {
        Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObject();
    }

    @Test
//    @Order(2)
    @Story("Создание Департамента")
    public void createDepartmentBlock() {
        Folder.builder().kind(Folder.DEPARTMENT).build().createObject();
    }

    @Test
//    @Order(3)
    @Story("Создание Папки")
    public void createFolder() {
        Folder.builder().kind(Folder.DEFAULT).build().createObject();
    }

}
