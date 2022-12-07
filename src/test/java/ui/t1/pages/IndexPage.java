package ui.t1.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.t1.pages.cloudCompute.*;
import ui.t1.pages.cloudDirector.CloudDirectorPage;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class IndexPage {
    final SelenideElement linkCloudEngine = $x("//a[.='T1 Cloud Engine']");
    final SelenideElement linkCloudDirector = $x("//a[.='Cloud Director']");
    final SelenideElement linkDisks = $x("//a[.='Диски']");
    final SelenideElement linkSshKeys = $x("//a[.='SSH-ключи']");
    final SelenideElement linkSnapshots = $x("//a[.='Снимки']");
    final SelenideElement linkVirtualMachines = $x("//a[.='Виртуальные машины']");
    final SelenideElement linkSecurityGroups = $x("//a[.='Группы безопасности']");
    final SelenideElement linkPublicIps = $x("//a[.='Публичные IP-адреса']");

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

    @Step("Переход на страницу Снимки")
    public SnapshotList goToSnapshots() {
        linkCloudEngine.click();
        linkSnapshots.click();
        return new SnapshotList();
    }

    @Step("Переход на страницу Публичные IP-адреса")
    public PublicIpList goToPublicIps() {
        linkCloudEngine.click();
        linkPublicIps.click();
        return new PublicIpList();
    }
}