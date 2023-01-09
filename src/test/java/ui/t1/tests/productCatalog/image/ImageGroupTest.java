package ui.t1.tests.productCatalog.image;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.t1.imageService.ImageGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.t1.imageService.ImageServiceSteps;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.t1.pages.ControlPanelIndexPage;

import java.util.Arrays;
import java.util.UUID;

@Feature("Образы.Группы образов")
public class ImageGroupTest extends BaseTest {

    private final String distro = UUID.randomUUID().toString();
    private final String title = "AT UI Group_" + UUID.randomUUID();

    @Test
    @TmsLink("1292898")
    @DisplayName("Просмотр списка групп образов")
    public void viewImageGroupsListTest() {
        new ControlPanelIndexPage().goToImageGroupsListPage()
                .checkImageGroupsListHeaders()
                .setRecordsPerPage(25)
                .setRecordsPerPage(50);
    }

    @Test
    @TmsLink("1292907")
    @DisplayName("Просмотр информации по группе")
    public void viewImageGroupInfoTest() {
        new ControlPanelIndexPage().goToImageGroupsListPage()
                .showImageGroupInfo(0)
                .hideImageGroupInfo(0);
    }

    @Test
    @TmsLink("1367064")
    @DisplayName("Добавление группы образов")
    public void addImageGroupTest() {
        String name = UUID.randomUUID().toString();
        String logoUrl = "https://api.s3.dev.t1.cloud/obzg62rng44tgnlpn5wdiztc:product-catalog/product_catalog_icons/ubuntu.png";
        ImageGroup imageGroup = ImageGroup.builder()
                .name(name).title(title).distro(distro).tags(Arrays.asList("tag 1", "tag 2"))
                .logo(logoUrl).build();
        new ControlPanelIndexPage().goToImageGroupsListPage()
                .addImageGroup(imageGroup)
                .checkAttributes(imageGroup);
        deleteByApi(imageGroup);
    }

    @Test
    @TmsLink("1368407")
    @DisplayName("Редактирование группы образов")
    public void editImageGroupTest() {
        String name = UUID.randomUUID().toString();
        ImageGroup imageGroup = ImageGroup.builder()
                .name(name).title(title).distro(distro).tags(Arrays.asList("tag 1", "tag 2"))
                .build().createObject();
        imageGroup.setName(UUID.randomUUID().toString());
        imageGroup.setTitle("New title");
        imageGroup.setTags(Arrays.asList("New tag"));
        new ControlPanelIndexPage().goToImageGroupsListPage()
                .editImageGroup(imageGroup)
                .checkAttributes(imageGroup);
    }

    @Test
    @TmsLink("1367069")
    @DisplayName("Удаление группы образов")
    public void deleteImageGroupTest() {
        String name = UUID.randomUUID().toString();
        ImageGroup imageGroup = ImageGroup.builder()
                .name(name).title(title).distro(distro).tags(Arrays.asList("tag"))
                .build().createObject();
        new ControlPanelIndexPage().goToImageGroupsListPage()
                .delete(imageGroup);
    }

    @Step("Удаление группы образов вызовом API")
    private void deleteByApi(ImageGroup imageGroup) {
        String id = ImageServiceSteps.getImageGroupByName(imageGroup.getName()).getId();
        ImageServiceSteps.deleteImageGroupById(id);
    }
}
