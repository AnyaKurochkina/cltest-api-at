package ui.t1.pages.cdn;

import io.qameta.allure.Step;
import ui.elements.Tab;

public class CdnPage {

    private final Tab sourceGroupsTab = Tab.byText("Группы источников");
    private final Tab resourcesTab = Tab.byText("Ресурсы");
    private final Tab certificateTab = Tab.byText("Пользовательские сертификаты");

    @Step("Переключение на вкладку Группы источников")
    public SourceGroupsTab switchToSourceGroupTab() {
        sourceGroupsTab.switchTo();
        return new SourceGroupsTab();
    }

    @Step("Переключение на вкладку Ресурсы")
    public ResourcesTab switchToResourceTab() {
        resourcesTab.switchTo();
        return new ResourcesTab();
    }

    @Step("Переключение на вкладку Пользовательские Сертификаты")
    public CertificateTab switchToCertificateTab() {
        certificateTab.switchTo();
        return new CertificateTab();
    }
}
