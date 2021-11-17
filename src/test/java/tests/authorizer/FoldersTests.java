package tests.authorizer;

import core.helper.Deleted;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.authorizer.Folder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Организационная структура")
@Feature("Папки")
@Tags({@Tag("regress"), @Tag("orgStructure"), @Tag("smoke")})
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
    @Deleted
    public void deleteFolder() {
        Folder.builder().kind(Folder.DEFAULT).build().createObject().deleteObject();
    }

    @Test
    @Order(5)
    @DisplayName("Удаление Департамента")
    @Deleted
    public void deleteDepartmentBlock() {
        Folder.builder().kind(Folder.DEPARTMENT).build().createObject().deleteObject();
    }

    @Test
    @Order(6)
    @DisplayName("Удаление Бизнес-блока")
    @Deleted
    public void deleteBusinessBlock() {
        Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObject().deleteObject();
    }

}
