package ui.t1.tests.productCatalog.image;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.t1.imageService.ImageGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.tests.productCatalog.BaseTest;
import ui.t1.pages.ControlPanelIndexPage;

import java.util.Arrays;
import java.util.UUID;

@Feature("Образы.Группы образов")
public class ImageGroupTest extends BaseTest {

    private final String distro = "Ubuntu";
    private final String title = "AT UI Image Group";

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
        ImageGroup imageGroup = ImageGroup.builder()
                .name(name).title(title).distro(distro).tags(Arrays.asList("tag 1", "tag 2"))
                .logoId("ubuntu").build();
        new ControlPanelIndexPage().goToImageGroupsListPage()
                .addImageGroup(imageGroup)
                .openEditDialog(imageGroup)
                .checkAttribures(imageGroup);
    }

    @Test
    @TmsLink("1368407")
    @DisplayName("Редактирование группы образов")
    public void editImageGroupTest() {
        String name = UUID.randomUUID().toString();
        ImageGroup imageGroup = ImageGroup.builder()
                .name(name).title(title).distro(distro).tags(Arrays.asList("tag 1", "tag 2"))
                .build().createObject();
        new ControlPanelIndexPage().goToImageGroupsListPage()
                .editImageGroup(imageGroup);
    }
}
