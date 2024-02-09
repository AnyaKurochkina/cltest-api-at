package ui.t1.tests.productCatalog.image;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.t1.imageService.Logo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.t1.imageService.ImageServiceSteps;
import ui.cloud.tests.productCatalog.ProductCatalogUITest;
import ui.t1.pages.ControlPanelIndexPage;

import java.util.UUID;

@Feature("Образы.Логотипы")
public class LogoTest extends ProductCatalogUITest {

    private final String logoUrl = "https://ibb.co/n7rGJKm";
    private final String distro = "test";

    @Test
    @TmsLink("1364689")
    @DisplayName("Просмотр списка логотипов")
    public void viewLogoListTest() {
        new ControlPanelIndexPage().goToLogoListPage()
                .checkHeaders()
                .setRecordsPerPage(25)
                .setRecordsPerPage(50);
    }

    @Test
    @TmsLink("1364735")
    @DisplayName("Добавление логотипа")
    public void addLogoTest() {
        checkRequiredFields();
        createLogo();
    }

    @Step("Добавление логотипа")
    private void createLogo() {
        String name = UUID.randomUUID().toString();
        Logo logo = Logo.builder()
                .name(name).logo(logoUrl).osDistro(distro).build();
        new ControlPanelIndexPage().goToLogoListPage()
                .createLogo(logo)
                .openEditDialog(logo)
                .checkAttributes(logo);
        deleteByApi(logo);
    }

    @Step("Проверка обязательности полей")
    private void checkRequiredFields() {
        String name = UUID.randomUUID().toString();
        new ControlPanelIndexPage().goToLogoListPage()
                .checkRequiredFields(Logo.builder().name("").logo(logoUrl).osDistro(distro).build())
                .checkRequiredFields(Logo.builder().name(name).logo("").osDistro(distro).build())
                .checkRequiredFields(Logo.builder().name(name).logo(logoUrl).osDistro("").build());
    }

    @Step("Удаление логотипа вызовом API")
    private void deleteByApi(Logo logo) {
        String id = ImageServiceSteps.getLogoByName(logo.getName()).getId();
        ImageServiceSteps.deleteLogoById(id);
    }

    @Step("Проверка валидации поля URL логотипа")
    private void checkUrlValidation() {
        String name = UUID.randomUUID().toString();
        String logoUrl = "im.wampi.ru/2022/12/15/free-icon-angel-257559.png";
        Logo logo = Logo.builder()
                .name(name).logo(logoUrl).osDistro(distro).build();
        new ControlPanelIndexPage().goToLogoListPage()
                .checkUrlValidation(logo);
    }

    @Test
    @TmsLink("1364748")
    @DisplayName("Редактирование логотипа")
    public void editLogoTest() {
        String name = UUID.randomUUID().toString();
        Logo logo = Logo.builder()
                .name(name).logo(logoUrl).osDistro(distro).build().createObject();
        Logo editedLogo = Logo.builder().name(UUID.randomUUID().toString())
                .logo("https://api.s3.dev.t1.cloud/obzg62rng44tgnlpn5wdiztc:product-catalog/product_catalog_icons/ubuntu.png")
                .osDistro("new distro").build();
        new ControlPanelIndexPage().goToLogoListPage().openEditDialog(logo).setAttributes(editedLogo)
                .openEditDialog(editedLogo).checkAttributes(editedLogo);
        deleteByApi(editedLogo);
    }

    @Test
    @TmsLink("1364759")
    @DisplayName("Удаление логотипа")
    public void deleteLogoTest() {
        String name = UUID.randomUUID().toString();
        Logo logo = Logo.builder()
                .name(name).logo(logoUrl).osDistro(distro).build().createObject();
        new ControlPanelIndexPage().goToLogoListPage().delete(logo);
    }
}
