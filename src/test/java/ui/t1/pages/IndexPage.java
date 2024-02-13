package ui.t1.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.cloud.pages.productCatalog.AuditPage;
import ui.elements.Button;
import ui.elements.Menu;
import ui.t1.pages.IAM.OrgStructurePage;
import ui.t1.pages.IAM.serviceAccounts.ServiceAccountsListPage;
import ui.t1.pages.IAM.users.UsersPage;
import ui.t1.pages.S3Storage.CloudStorageS3;
import ui.t1.pages.bills.BillsPage;
import ui.t1.pages.cdn.CdnPage;
import ui.t1.pages.cloudDirector.CloudDirectorPage;
import ui.t1.pages.cloudEngine.backup.BackupsList;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.pages.cloudEngine.vpc.*;
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
    private final Button linkCloudStorageS3 = Button.byXpath("//a[.='Объектные хранилища']");
    private final Button linkOldCloudStorageS3 = Button.byXpath("//a[@href='/buckets']");
    private final Button linkNewCloudStorageS3 = Button.byXpath("//a[@href='/new-buckets']");
    private final Button linkResources = Button.byXpath("//a[.='Ресурсы']");
    private final Button linkSupportCenter = Button.byXpath("//a[.='Центр поддержки']");
    private final Button linkNotifications = Button.byXpath("//a[.='Уведомления']");
    private final Button linkMySubscriptions = Button.byXpath("//a[.='Мои подписки']");
    private final Button linkSubscriptionsByAdmin = Button.byXpath("//a[.='Подписки пользователей организации']");
    private final Button linkCloudEngine = Button.byXpath("//a[.='Cloud Engine']");
    private final Button linkCloudDirector = Button.byXpath("//a[.='Cloud Director']");
    private final Button linkDisks = Button.byXpath("//a[.='Диски']");
    private final Button linkSshKeys = Button.byXpath("//a[.='SSH-ключи']");
    private final Button linkSnapshots = Button.byXpath("//a[.='Снимки']");
    private final Button linkVirtualMachines = Button.byXpath("//a[.='Серверы']");
    private final Button linkSecurityGroups = Button.byXpath("//a[.='Группы безопасности']");
    private final Button linkPublicIps = Button.byXpath("//a[.='Публичные IP-адреса']");
    private final Button linkVirtualIps = Button.byXpath("//a[.='Виртуальные IP-адреса']");
    private final Button linkRouters = Button.byXpath("//a[.='Маршрутизаторы']");
    private final Button linkImages = Button.byXpath("//a[.='Образы']");
    private final Button linkNetworkInterfaces = Button.byXpath("//a[.='Сетевые интерфейсы']");
    private final Button linkHistory = Button.byXpath("//a[.='История действий']");
    private final Button linkNetworks = Button.byXpath("//a[.='Сети']");
    private final Button linkTools = Button.byXpath("//a[.='Инструменты']");
    private final Button linkIAM = Button.byXpath("//a[.='IAM и Управление']");
    private final Button linkUsers = Button.byXpath("//a[.='Пользователи']");
    private final Button linkOrgStructure = Button.byXpath("//a[.='Орг. структура']");
    private final Button linkServiceAccounts = Button.byXpath("//a[.='Сервисные аккаунты']");
    private final Button linkAudit = Button.byXpath("//a[.='Аудит']");
    private final Button linkBills = Button.byXpath("//a[.='Счета']");
    private final SelenideElement linkProfile = $x("//span/button[@data-dimension ='m']");
    private final SelenideElement changeContext = $x("//*[name() = 'path' and @d = 'M5.226 8.56c0-.18.07-.35.21-.48.27-.24.68-.22.92.04l5.74 6.37 5.55-6.41a.65.65 0 01.92-.04c.26.24.28.65.04.92l-5.99 6.9c-.28.31-.76.31-1.04 0L5.396 9a.627.627 0 01-.17-.44z']/parent::*/parent::*");
    private final Button linkBackups = Button.byXpath("//a[.='Резервные копии']");
    private final Button linkPlacementPolicy = Button.byXpath("//a[.='Политики размещения']");
    private final Button linkCdn = Button.byXpath("//a[.='Cloud CDN']");

    public IndexPage() {
        //Ожидание загрузки /tenant
        Waiting.sleep(3000);
    }

    @Step("Переход на главную страницу")
    public static void go() {
        $x("//*[@title = 'Главная']").shouldBe(Condition.visible).click();
    }

    public Profile goToProfile() {
        linkProfile.shouldBe(Condition.visible).click();
        return new Profile();
    }

    @Step("Переход на страницу S3 Cloud Storage")
    public CloudStorageS3 goToS3CloudStoragePage() {
        linkCloudStorageS3.click();
        linkOldCloudStorageS3.click();
        return new CloudStorageS3();
    }

    @Step("Переход на страницу Объектное хранилище S3")
    public CloudStorageS3 goToNewS3CloudStoragePage() {
        linkCloudStorageS3.click();
        linkNewCloudStorageS3.click();
        return new CloudStorageS3();
    }

    @Step("Переход на страницу Cloud Engine")
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

    @Step("Переход на страницу Сервисные аккаунты")
    public ServiceAccountsListPage goToServiceAccounts() {
        linkIAM.click();
        linkServiceAccounts.click();
        return new ServiceAccountsListPage();
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

    @Step("Переход на страницу Резервные копии")
    public BackupsList goToBackups() {
        linkCloudEngine.click();
        linkBackups.click();
        return new BackupsList();
    }

    @Step("Переход на страницу 'Политики размещения'")
    public PlacementPolicyList goToPlacementPolicy() {
        linkCloudEngine.click();
        linkPlacementPolicy.click();
        return new PlacementPolicyList();
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

    @Step("Переход на страницу Маршрутизаторы")
    public RouterList goToRouters() {
        linkCloudEngine.click();
        linkRouters.click();
        return new RouterList();
    }

    @Step("Переход на страницу Cloud CDN")
    public CdnPage goToCdn() {
        linkCdn.click();
        return new CdnPage();
    }

    @Step("Отключить Cloud Engine")
    public void disconnectCloudEngine() {
        SelenideElement btnAction = getActionsMenuButton("Cloud Engine");
        Menu.byElement(btnAction).select("Отключить услугу");
        Button.byText("Отключить").click();
        Waiting.findWithRefresh(() -> !btnAction.isDisplayed(), Duration.ofMinutes(1));
        btnAction.shouldNotBe(Condition.exist);
    }

    @Step("Переход в Центр уведомлений на страницу Мои уведомления")
    public NotificationsPage goToNotificationCenter() {
        linkSupportCenter.click();
        linkNotifications.click();
        return new NotificationsPage();
    }

    @Step("Переход в Центр уведомлений на страницу Мои подписки")
    public MySubscriptionsPage goToMySubscriptions() {
        linkSupportCenter.click();
        linkNotifications.click();
        linkMySubscriptions.click();

        return new MySubscriptionsPage();
    }

    @Step("Переходим в Центр уведомлений на страницу Подписки пользователей организации")
    public SubscribeUsersPage goToUsersSubscriptions() {
        linkSupportCenter.click();
        linkNotifications.click();
        linkSubscriptionsByAdmin.click();

        return new SubscribeUsersPage();
    }

    @Step("Переход в модальное окно изменения контекста")
    public ContextDialog goToContextDialog() {
        changeContext.shouldBe(activeCnd).shouldBe(clickableCnd).click();
        return new ContextDialog();
    }

    @Step("Проверка отображения имени {contextName} контекста")
    public boolean isContextNameDisplayed(String contextName) {
        return $x("//div[text() = '{}']", contextName).isDisplayed();
    }

    @Step("Переход на страницу Инструменты.Аудит")
    public AuditPage goToPortalAuditPage() {
        linkTools.click();
        Waiting.sleep(1000); //чтобы подгрузились последние изменения
        linkAudit.click();
        return new AuditPage();
    }

    @Step("Переход на страницу Инструменты.Счета")
    public BillsPage goToBillsPage() {
        linkTools.click();
        linkBills.click();
        return new BillsPage();
    }
}