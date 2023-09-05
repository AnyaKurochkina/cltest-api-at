package ui.t1.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.elements.Button;
import ui.elements.Menu;
import ui.t1.pages.IAM.OrgStructurePage;
import ui.t1.pages.IAM.users.UsersPage;
import ui.t1.pages.S3Storage.CloudStorageS3;
import ui.t1.pages.cloudDirector.CloudDirectorPage;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.pages.cloudEngine.vpc.NetworkList;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.pages.cloudEngine.vpc.SecurityGroupList;
import ui.t1.pages.cloudEngine.vpc.VirtualIpList;
import ui.t1.pages.supportCenter.MySubscriptionsPage;
import ui.t1.pages.supportCenter.NotificationsPage;
import ui.t1.pages.supportCenter.SubscribeUsersPage;

import java.time.Duration;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.cloud.pages.orders.IProductPage.getActionsMenuButton;

@Getter
public class IndexPage {
    Button linkCloudStorageS3 = Button.byXpath("//a[.='Cloud Storage S3']");
    Button linkResources = Button.byXpath("//a[.='Ресурсы']");
    Button linkSupportCenter = Button.byXpath("//a[.='Центр поддержки']");
    Button linkNotifications = Button.byXpath("//a[.='Уведомления']");
    Button linkMySubscriptions = Button.byXpath("//a[.='Мои подписки']");
    Button linkSubscriptionsByAdmin = Button.byXpath("//a[.='Подписки пользователей организации']");
    Button linkCloudEngine = Button.byXpath("//a[.='T1 Cloud Engine']");
    Button linkCloudDirector = Button.byXpath("//a[.='Cloud Director']");
    Button linkDisks = Button.byXpath("//a[.='Диски']");
    Button linkSshKeys = Button.byXpath("//a[.='SSH-ключи']");
    Button linkSnapshots = Button.byXpath("//a[.='Снимки']");
    Button linkVirtualMachines = Button.byXpath("//a[.='Серверы']");
    Button linkSecurityGroups = Button.byXpath("//a[.='Группы безопасности']");
    Button linkPublicIps = Button.byXpath("//a[.='Публичные IP-адреса']");
    Button linkVirtualIps = Button.byXpath("//a[.='Виртуальные IP-адреса']");
    Button linkImages = Button.byXpath("//a[.='Образы']");
    Button linkNetworkInterfaces = Button.byXpath("//a[.='Сетевые интерфейсы']");
    Button linkHistory = Button.byXpath("//a[.='История действий']");
    Button linkNetworks = Button.byXpath("//a[.='Сети']");
    Button linkTools = Button.byXpath("//a[.='Инструменты']");
    Button linkIAM = Button.byXpath("//a[.='IAM и Управление']");
    Button linkUsers = Button.byXpath("//a[.='Пользователи']");
    Button linkOrgStructure = Button.byXpath("//a[.='Орг. структура']");
    SelenideElement linkProfile = $x("//span/button[@data-dimension ='m']");
    SelenideElement changeContext = $x("//*[name() = 'path' and @d = 'M5.226 8.56c0-.18.07-.35.21-.48.27-.24.68-.22.92.04l5.74 6.37 5.55-6.41a.65.65 0 01.92-.04c.26.24.28.65.04.92l-5.99 6.9c-.28.31-.76.31-1.04 0L5.396 9a.627.627 0 01-.17-.44z']/parent::*/parent::*");

    @Step("Переход на главную страницу")
    public static void go() {
        $x("//*[@title = 'Главная']").shouldBe(Condition.visible).click();
    }

    public Profile goToProfile(){
        linkProfile.shouldBe(Condition.visible).click();
        return new Profile();
    }

    @Step("Переход на страницу S3 Cloud Storage")
    public CloudStorageS3 goToS3CloudStoragePage() {
        linkCloudStorageS3.click();
        return new CloudStorageS3();
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

    @Step("Переход на страницу Пользователи")
    public UsersPage goToUsers() {
        linkIAM.click();
        linkUsers.click();
        return new UsersPage();
    }

    @Step("Переход на страницу Орг. структура")
    public OrgStructurePage goToOrgStructure() {
        linkIAM.click();
        linkOrgStructure.click();
        return new OrgStructurePage();
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

    @Step("Переход на страницу Публичные IP-адреса")
    public VirtualIpList goToVirtualIps() {
        linkCloudEngine.click();
        linkVirtualIps.click();
        return new VirtualIpList();
    }

    @Step("Отключить Cloud Engine")
    public void disconnectCloudEngine() {
        SelenideElement btnAction = getActionsMenuButton("T1 Cloud Engine");
        Menu.byElement(btnAction).select("Отключить услугу");
        Button.byText("Отключить").click();
        Waiting.findWithRefresh(() -> !btnAction.isDisplayed(), Duration.ofMinutes(1));
        btnAction.shouldNotBe(Condition.exist);
    }

    @Step("Переход в Центр уведомлений на страницу Мои уведомления")
    public NotificationsPage goToNotificationCenter(){
        linkSupportCenter.click();
        linkNotifications.click();
        return new NotificationsPage();
    }

    @Step("Переход в Центр уведомлений на страницу Мои подписки")
    public MySubscriptionsPage goToMySubscriptions(){
        linkSupportCenter.click();
        linkNotifications.click();
        linkMySubscriptions.click();

        return new MySubscriptionsPage();
    }

    @Step("Переходим в Центр уведомлений на страницу Подписки пользователей организации")
    public SubscribeUsersPage goToUsersSubscriptions(){
        linkSupportCenter.click();
        linkNotifications.click();
        linkSubscriptionsByAdmin.click();

        return new SubscribeUsersPage();
    }
    @Step("Переход в модальное окно изменения контекста")
    public ContextDialog changeContext(){
        changeContext.shouldBe(activeCnd).shouldBe(clickableCnd).click();
        return new ContextDialog();
    }

    @Step("Проверка отображения имени {contextName} контекста")
    public boolean isContextNameDisplayed(String contextName){
        Selenide.refresh();
        return $x("//div[text() = '{}']", contextName).shouldBe(Condition.visible).isDisplayed();
    }
}