package ui.t1.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.t1.pages.cloudCompute.CloudEnginePage;
import ui.t1.pages.cloudCompute.SecurityGroupsPage;
import ui.t1.pages.cloudCompute.SshKeysPage;
import ui.t1.pages.cloudCompute.VirtualMachinesPage;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class IndexPage {
    final SelenideElement linkCloudEngine = $x("//a[.='T1 Cloud Engine']");
    final SelenideElement linkCloudDirector = $x("//a[.='Cloud Director']");
    final SelenideElement linkSshKeys = $x("//a[.='SSH-ключи']");
    final SelenideElement linkVirtualMachines = $x("//a[.='Виртуальные машины']");
    final SelenideElement linkSecurityGroups = $x("//a[.='Группы безопасности']");

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
        linkSshKeys.click();
        return new SshKeysPage();
    }

    @Step("Переход на страницу Виртуальные машины")
    public VirtualMachinesPage goToVirtualMachine() {
        linkVirtualMachines.click();
        return new VirtualMachinesPage();
    }

    @Step("Переход на страницу Группы безопасности")
    public SecurityGroupsPage goToSecurityGroups() {
        linkSecurityGroups.click();
        return new SecurityGroupsPage();
    }
}