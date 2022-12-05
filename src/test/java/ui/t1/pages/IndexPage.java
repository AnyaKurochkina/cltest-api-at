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
    final SelenideElement linkVirtualMachines = $x("//a[.='Виртуальные машины']");
    final SelenideElement linkSecurityGroups = $x("//a[.='Группы безопасности']");
    final SelenideElement linkPublicIps = $x("//a[.='Публичные IP-адреса']");

    @Step("Переход на страницу T1 Cloud Engine")
    public CloudEnginePage goToCloudEngine() {
        linkCloudEngine.click();
        return new CloudEnginePage();
    }

    @Step("Переход на страницу Cloud Director")
    public CloudDirectorPage goToCloudDirector() {
        linkCloudDirector.click();
        return new CloudDirectorPage();
    }

    @Step("Переход на страницу SSH-ключи")
    public SshKeysPage goToSshKeys() {
        linkCloudEngine.click();
        linkSshKeys.click();
        return new SshKeysPage();
    }

    @Step("Переход на страницу Виртуальные машины")
    public VmsPage goToVirtualMachine() {
        linkCloudEngine.click();
        linkVirtualMachines.click();
        return new VmsPage();
    }

    @Step("Переход на страницу Группы безопасности")
    public SecurityGroupsPage goToSecurityGroups() {
        linkCloudEngine.click();
        linkSecurityGroups.click();
        return new SecurityGroupsPage();
    }

    @Step("Переход на страницу Диски")
    public DisksPage goToDisks() {
        linkCloudEngine.click();
        linkDisks.click();
        return new DisksPage();
    }

    @Step("Переход на страницу Публичные IP-адреса")
    public PublicIpsPage goToPublicIps() {
        linkCloudEngine.click();
        linkPublicIps.click();
        return new PublicIpsPage();
    }
}