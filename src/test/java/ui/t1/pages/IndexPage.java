package ui.t1.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import ui.elements.Button;
import ui.elements.Menu;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.pages.cloudDirector.CloudDirectorPage;
import ui.t1.pages.cloudEngine.vpc.NetworkList;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.pages.cloudEngine.vpc.SecurityGroupList;

import static core.helper.StringUtils.$$x;
import static core.helper.StringUtils.$x;
import static ui.cloud.pages.IProductPage.getBtnAction;


@Getter
public class IndexPage {
    final SelenideElement linkCloudEngine = $x("//a[.='T1 Cloud Engine']");
    final SelenideElement linkCloudDirector = $x("//a[.='Cloud Director']");
    final SelenideElement linkDisks = $x("//a[.='Диски']");
    final SelenideElement linkSshKeys = $x("//a[.='SSH-ключи']");
    final SelenideElement linkSnapshots = $x("//a[.='Снимки']");
    final SelenideElement linkVirtualMachines = $x("//a[.='Серверы']");
    final SelenideElement linkSecurityGroups = $x("//a[.='Группы безопасности']");
    final SelenideElement linkPublicIps = $x("//a[.='Публичные IP-адреса']");
    final SelenideElement linkImages = $x("//a[.='Образы']");
    final SelenideElement linkNetworkInterfaces = $x("//a[.='Сетевые интерфейсы']");
    final SelenideElement linkHistory = $x("//a[.='История действий']");
    final SelenideElement linkNetworks = $x("//a[.='Сети']");

    final ElementsCollection linkProfile = $$x("//*[@data-testid='topbar-menu-profile']");

    public static void go() {
        $x("//img[contains(@alt,'logo')]").shouldBe(Condition.visible).click();
    }

    public Profile goToProfile(){
        Menu.byElement(linkProfile.should(CollectionCondition.anyMatch("", WebElement::isDisplayed)).filter(Condition.visible).first()).select("Профиль");
        return new Profile();
    }

    @Step("Переход на страницу T1 Cloud Engine")
    public CloudEngine goToCloudEngine() {
        linkCloudEngine.click();
        return new CloudEngine();
    }

    @Step("Переход на страницу Cloud Director")
    public CloudDirectorPage goToCloudDirector() {
        linkCloudDirector.click();
        return new CloudDirectorPage();
    }

    @Step("Переход на страницу История действий")
    public ComputeHistory goToHistory() {
        linkCloudEngine.click();
        linkHistory.click();
        return new ComputeHistory();
    }

    @Step("Переход на страницу SSH-ключи")
    public SshKeyList goToSshKeys() {
        linkCloudEngine.click();
        linkSshKeys.click();
        return new SshKeyList();
    }

    @Step("Переход на страницу Виртуальные машины")
    public VmList goToVirtualMachine() {
        linkCloudEngine.click();
        linkVirtualMachines.click();
        return new VmList();
    }

    @Step("Переход на страницу Группы безопасности")
    public SecurityGroupList goToSecurityGroups() {
        linkCloudEngine.click();
        linkSecurityGroups.click();
        return new SecurityGroupList();
    }

    @Step("Переход на страницу Диски")
    public DiskList goToDisks() {
        linkCloudEngine.click();
        linkDisks.click();
        return new DiskList();
    }

    @Step("Переход на страницу Сетевые интерфейсы")
    public NetworkList goToNetworks() {
        linkCloudEngine.click();
        linkNetworks.click();
        return new NetworkList();
    }

    @Step("Переход на страницу Сетевые интерфейсы")
    public NetworkInterfaceList goToNetworkInterfaces() {
        linkCloudEngine.click();
        linkNetworkInterfaces.click();
        return new NetworkInterfaceList();
    }

    @Step("Переход на страницу Снимки")
    public SnapshotList goToSnapshots() {
        linkCloudEngine.click();
        linkSnapshots.click();
        return new SnapshotList();
    }

    @Step("Переход на страницу Образы")
    public ImageList goToImages() {
        linkCloudEngine.click();
        linkImages.click();
        return new ImageList();
    }

    @Step("Переход на страницу Публичные IP-адреса")
    public PublicIpList goToPublicIps() {
        linkCloudEngine.click();
        linkPublicIps.click();
        return new PublicIpList();
    }

    @Step("Отключить Cloud Engine")
    public void disconnectCloudEngine() {
        Menu.byElement(getBtnAction("T1 Cloud Engine")).select("Отключить услугу");
        Button.byText("Отключить").click();
        //Todo: дальнейшие действия пока не известны
    }
}