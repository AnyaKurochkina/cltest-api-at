package ui.t1.pages.S3Storage;

import io.qameta.allure.Step;
import ui.elements.Button;
import ui.t1.pages.S3Storage.AccessRules.AccessRulesLayer;
import ui.t1.pages.S3Storage.CORS.CORSLayer;
import ui.t1.pages.S3Storage.IncompleteDownloads.IncompleteDownloadsLayer;
import ui.t1.pages.S3Storage.LifeCycle.LifeCycleLayer;
import ui.t1.pages.S3Storage.Objects.ObjectsLayer;
import ui.t1.pages.S3Storage.WebSite.WebSiteLayer;

import static com.codeborne.selenide.Selenide.page;

public class AbstractLayerS3<PageObjectClass> {
    private Button namedLayer;

    public AbstractLayerS3(String name){
        namedLayer = Button.byText(name);
    }

    public AbstractLayerS3(){
    }

    public PageObjectClass gotoLayer(Class<PageObjectClass> pageObjectClass){
        this.namedLayer.click();
        return page(pageObjectClass);
    }

    @Step("Переход на вкладку 'CORS' в бакете")
    public CORSLayer gotoCORS(){
        return new CORSLayer("CORS").gotoLayer(CORSLayer.class);
    }

    @Step("Переход на вкладку 'Жизненный цикл' в бакете")
    public LifeCycleLayer gotoLifeCycle(){
        return new LifeCycleLayer("Жизненный цикл").gotoLayer(LifeCycleLayer.class);
    }

    @Step("Переход на вкладку 'Права доступа' в бакете")
    public AccessRulesLayer gotoAccessRulesLayer(){
        return new AccessRulesLayer("Права доступа").gotoLayer(AccessRulesLayer.class);
    }

    @Step("Переход на вкладку 'Незавершенные загрузки' в бакете")
    public IncompleteDownloadsLayer gotoAccessIncompleteDownloadsLayer(){
        return new IncompleteDownloadsLayer("Незавершенные загрузки").gotoLayer(IncompleteDownloadsLayer.class);
    }

    @Step("Переход на вкладку 'Веб-сайт' в бакете")
    public WebSiteLayer gotoAccessWebSiteLayer(){
        return new WebSiteLayer("Веб-сайт").gotoLayer(WebSiteLayer.class);
    }

    @Step("Переход на вкладку Объекты' в бакете")
    public ObjectsLayer gotoObjectsLayer(){
        return new ObjectsLayer("Объекты").gotoLayer(ObjectsLayer.class);
    }
}
