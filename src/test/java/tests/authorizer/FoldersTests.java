package tests.authorizer;

import core.helper.Deleted;
import io.qameta.allure.*;
import models.authorizer.Folder;
import models.authorizer.Organization;
import org.junit.OrderLabel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import steps.authorizer.AuthorizerSteps;
import tests.Tests;

@Epic("Организационная структура")
@Feature("Папки")
@Tags({@Tag("regress"), @Tag("orgStructure3"), @Tag("smoke")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class FoldersTests extends Tests {

    @Test
    @Order(1)
    @DisplayName("Создание Бизнес-блока")
    void createBusinessBlock() {
        Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObject();
    }

    @Test
    @Order(2)
    @DisplayName("Создание Департамента")
    void createDepartmentBlock() {
        Folder.builder().kind(Folder.DEPARTMENT).build().createObject();
    }

    @Test
    @Order(3)
    @DisplayName("Создание Папки")
    void createFolder() {
        Folder.builder().kind(Folder.DEFAULT).build().createObject();
    }

    @Test
    @Order(4)
    @DisplayName("Удаление Папки")
    @Deleted(Folder.class)
    public void deleteFolder() {
        Folder.builder().name("FOR_DELETE").kind(Folder.DEFAULT).build().createObject().deleteObject();
    }

    //todo: fix delete
    @Test
    @Order(5)
    @DisplayName("Удаление Департамента")
    @Deleted(Folder.class)
    public void deleteDepartmentBlock() {
        Folder.builder().name("FOR_DELETE").kind(Folder.DEPARTMENT).build().createObject().deleteObject();
    }

    //todo: fix delete
    @Test
    @Order(6)
    @DisplayName("Удаление Бизнес-блока")
    @Deleted(Folder.class)
    public void deleteBusinessBlock() {
        Folder.builder().name("FOR_DELETE").kind(Folder.BUSINESS_BLOCK).build().createObject().deleteObject();
    }

}
