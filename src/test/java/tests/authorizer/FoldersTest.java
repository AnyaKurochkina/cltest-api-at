package tests.authorizer;

import core.helper.MarkDelete;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.authorizer.Folder;
import org.junit.DisabledIfEnv;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.Tests;

@Epic("Организационная структура")
@Feature("Папки")
@Tags({@Tag("regress"), @Tag("orgstructure"), @Tag("smoke"), @Tag("prod")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
public class FoldersTest extends Tests {

    @Test
    @Order(1)
    @TmsLink("377750")
    @DisabledIfEnv("prod")
    @DisplayName("Создание Бизнес-блока")
    void createBusinessBlock() {
        Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObject();
    }

    @Test
    @Order(2)
    @TmsLink("377751")
    @DisabledIfEnv("prod")
    @DisplayName("Создание Департамента")
    void createDepartmentBlock() {
        Folder.builder().kind(Folder.DEPARTMENT).build().createObject();
    }

    @Test
    @Order(3)
    @TmsLink("377752")
    @DisplayName("Создание Папки")
    void createFolder() {
        Folder.builder().kind(Folder.DEFAULT).build().createObject();
    }

    @Test
    @Order(4)
    @TmsLink("377753")
    @DisplayName("Удаление Папки")
    @MarkDelete
    public void deleteFolder() {
        Folder.builder().kind(Folder.DEFAULT).build().createObject().deleteObject();
    }

    @Test
    @Order(5)
    @TmsLink("647022")
    @DisabledIfEnv("prod")
    @DisplayName("Удаление Департамента")
    @MarkDelete
    public void deleteDepartmentBlock() {
        Folder.builder().kind(Folder.DEPARTMENT).build().createObject().deleteObject();
    }

    @Test
    @Order(6)
    @TmsLink("647024")
    @DisabledIfEnv("prod")
    @DisplayName("Удаление Бизнес-блока")
    @MarkDelete
    public void deleteBusinessBlock() {
        Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObject().deleteObject();
    }

}
