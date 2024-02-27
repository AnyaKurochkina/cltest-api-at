package api.cloud.authorizer;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Folder;
import org.junit.DisabledIfEnv;
import org.junit.MarkDelete;
import org.junit.jupiter.api.*;

@Epic("Организационная структура")
@Feature("Папки")
@Tags({@Tag("regress"), @Tag("orgstructure"), @Tag("smoke"), @Tag("prod")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Tag("health_check")
    @Order(3)
    @TmsLink("377752")
    @DisplayName("Создание Папки")
    void createFolder() {
        Folder.builder().kind(Folder.DEFAULT).build().createObject();
    }

    @Test
    @Order(4)
    @TmsLink("720840")
    @DisplayName("Изменение Папки")
    void editFolder() {
        try (Folder folder = Folder.builder().kind(Folder.DEFAULT).build().createObjectExclusiveAccess()) {
            folder.setTitle("newTitle");
            folder.edit();
        }
    }

    @Test
    @Tag("health_check")
    @Order(5)
    @TmsLink("377753")
    @DisplayName("Удаление Папки")
    @MarkDelete
    public void deleteFolder() {
        Folder.builder().kind(Folder.DEFAULT).build().createObject().deleteObject();
    }

    @Test
    @Order(6)
    @TmsLink("647022")
    @DisabledIfEnv("prod")
    @DisplayName("Удаление Департамента")
    @MarkDelete
    public void deleteDepartmentBlock() {
        Folder.builder().kind(Folder.DEPARTMENT).build().createObject().deleteObject();
    }

    @Test
    @Order(7)
    @TmsLink("647024")
    @DisabledIfEnv("prod")
    @DisplayName("Удаление Бизнес-блока")
    @MarkDelete
    public void deleteBusinessBlock() {
        Folder.builder().kind(Folder.BUSINESS_BLOCK).build().createObject().deleteObject();
    }

}
