package ui.t1.pages.S3Storage.WebSite;

import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;

public class WebSiteSettingsModal extends AbstractLayerS3<WebSiteSettingsModal> {

    public WebSiteSettingsModal()
    {

    }

    @Step("Установка названия индексной страницы '{name}'")
    public WebSiteSettingsModal setIndexName(String name)
    {
        Input.byName("index_page").setValue(name);
        return this;
    }

    @Step("Установка названия страницы ошибки'{name}'")
    public WebSiteSettingsModal setErrorName(String name)
    {
        Input.byName("error_page").setValue(name);
        return this;
    }

    @Step("Закрытие модального окна редактирования веб-сайта")
    public WebSiteLayer cancelEdit()
    {
        Button.byText("Отменить").click();
        return new WebSiteLayer();
    }

    @Step("Применение изменений редактирования веб-сайта")
    public WebSiteLayer saveEdit()
    {
        Button.byText("Сохранить").click();
        Alert.green("Данные веб-сайта успешно изменены");
        return new WebSiteLayer();
    }
}
