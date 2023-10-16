package ui.t1.pages.S3Storage.WebSite;

import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.DataTable;
import ui.elements.Switch;
import ui.t1.pages.S3Storage.AbstractLayerS3;

public class WebSiteLayer extends AbstractLayerS3<WebSiteLayer> {

    private DataTable lifeCycleList;

    public WebSiteLayer(String name)
    {
        super(name);
    }

    public WebSiteLayer()
    {

    }

    @Step("Включение режима веб-сайта '{mode}'")
    public WebSiteLayer setWebSiteMode(Boolean mode)
    {
        Waiting.sleep(3000);
        Switch.byText("Режим веб-сайта").setEnabled(mode);
        Alert.green("Режим веб-сайта успешно изменен");
        return this;
    }

    @Step("Включение режима веб-сайта '{mode}'")
    public WebSiteSettingsModal openWebSiteSettings()
    {
        Button.byText("Редактировать").click();
        return new WebSiteSettingsModal();
    }


}
