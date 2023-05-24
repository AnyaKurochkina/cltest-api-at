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
import ui.t1.pages.cloudDirector.CloudDirectorPage;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.pages.cloudEngine.vpc.NetworkList;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.pages.cloudEngine.vpc.SecurityGroupList;
import ui.t1.pages.supportCenter.Notifications;

import static core.helper.StringUtils.$$x;
import static core.helper.StringUtils.$x;
import static ui.cloud.pages.orders.IProductPage.getBtnAction;


@Getter
public class IndexPage {
    Button linkResources = Button.byXpath("//a[.='Ресурсы']");
    Button linkTools = Button.byXpath("//a[.='Инструменты']");
    Button linkSupportCenter = Button.byXpath("//a[.='Центр поддержки']");
    Button linkNotifications = Button.byXpath("//a[.='Уведомления']");
    Button linkCloudEngine = Button.byXpath("//a[.='T1 Cloud Engine']");
    Button linkCloudDirector = Button.byXpath("//a[.='Cloud Director']");
    Button linkDisks = Button.byXpath("//a[.='Диски']");
    Button linkSshKeys = Button.byXpath("//a[.='SSH-ключи']");
    Button linkSnapshots = Button.byXpath("//a[.='Снимки']");
    Button linkVirtualMachines = Button.byXpath("//a[.='Серверы']");
    Button linkSecurityGroups = Button.byXpath("//a[.='Группы безопасности']");
    Button linkPublicIps = Button.byXpath("//a[.='Публичные IP-адреса']");
    Button linkImages = Button.byXpath("//a[.='Образы']");
    Button linkNetworkInterfaces = Button.byXpath("//a[.='Сетевые интерфейсы']");
    Button linkHistory = Button.byXpath("//a[.='История действий']");
    Button linkNetworks = Button.byXpath("//a[.='Сети']");

    final ElementsCollection linkProfile = $$x("//*[@data-testid='topbar-menu-profile']");

    public static void go() {
        $x("(//img[contains(@alt,'logo')])[2]").shouldBe(Condition.visible).click();
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
        SelenideElement btnAction = getBtnAction("T1 Cloud Engine");
        Menu.byElement(btnAction).select("Отключить услугу");
        Button.byText("Отключить").click();
        btnAction.shouldNotBe(Condition.exist);
        Menu.byElement(getBtnAction("T1 Cloud Engine")).select("Отключить услугу");
        Button.byText("Отключить").click();
    }

    @Step("Переход в Центр уведомлений")
    public Notifications goToNotificationCenter(){
        linkSupportCenter.click();
        linkNotifications.click();
        return new Notifications();
    }
}