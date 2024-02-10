package ui.t1.pages.cdn.resource;

import io.qameta.allure.Step;
import ui.elements.Tab;

public class ResourcePage {

    private final Tab generalTab = Tab.byText("Обзор");
    private final Tab httpHeadersTab = Tab.byText("HTTP -заголовки и методы");

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
}
