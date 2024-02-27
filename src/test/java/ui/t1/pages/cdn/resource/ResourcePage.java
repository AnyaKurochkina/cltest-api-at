package ui.t1.pages.cdn.resource;

import io.qameta.allure.Step;
import ui.elements.Tab;

public class ResourcePage {

    private final Tab generalTab = Tab.byText("Обзор");
    private final Tab httpHeadersTab = Tab.byText("HTTP -заголовки и методы");
    private final Tab shieldingOfSourcesTab = Tab.byText("Экранирование источников");
    private final Tab imageCompressingTab = Tab.byText("Сжатие изображений");

    @Step("Переключение на вкладку Обзор")
    public ResourceGeneralTab switchToResourceGeneralTab(String resourceName) {
        generalTab.switchTo();
        return new ResourceGeneralTab(resourceName);
    }

    @Step("Переключение на вкладку HTTP - заголовки и методы")
    public HttpHeadersTab switchToHttpHeadersTab() {
        httpHeadersTab.switchTo();
        return new HttpHeadersTab();
    }

    @Step("Переключение на вкладку Экранирование источников")
    public ShieldingOfSourcesTab switchToShieldingOfSources() {
        shieldingOfSourcesTab.switchTo();
        return new ShieldingOfSourcesTab();
    }

    @Step("Переключение на вкладку Сжатие изображений")
    public ImageCompressingTab switchToImageCompressing() {
        imageCompressingTab.switchTo();
        return new ImageCompressingTab();
    }
}
