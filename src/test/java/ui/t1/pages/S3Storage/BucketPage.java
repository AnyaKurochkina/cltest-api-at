package ui.t1.pages.S3Storage;

import io.qameta.allure.Step;
import ui.elements.Tab;
import ui.t1.pages.S3Storage.AccessRules.AccessRulesLayer;
import ui.t1.pages.S3Storage.CORS.CORSLayer;
import ui.t1.pages.S3Storage.IncompleteDownloads.IncompleteDownloadsLayer;
import ui.t1.pages.S3Storage.LifeCycle.LifeCycleLayer;
import ui.t1.pages.S3Storage.Objects.ObjectsLayer;
import ui.t1.pages.S3Storage.WebSite.WebSiteLayer;

public class BucketPage {

    @Step("Переход на вкладку 'CORS' в бакете")
    public CORSLayer gotoCORS(){
        Tab.byText("CORS").switchTo();
        return new CORSLayer();
    }

    @Step("Переход на вкладку 'Жизненный цикл' в бакете")
    public LifeCycleLayer gotoLifeCycle(){
        Tab.byText("Жизненный цикл").switchTo();
        return new LifeCycleLayer();
    }

    @Step("Переход на вкладку 'Правила доступа' в бакете")
    public AccessRulesLayer gotoAccessRulesLayer(){
        Tab.byText("Правила доступа").switchTo();
        return new AccessRulesLayer();
    }

    @Step("Переход на вкладку 'Незавершенные загрузки' в бакете")
    public IncompleteDownloadsLayer gotoAccessIncompleteDownloadsLayer(){
        Tab.byText("Незавершенные загрузки").switchTo();
        return new IncompleteDownloadsLayer();
    }

    @Step("Переход на вкладку 'Веб-сайт' в бакете")
    public WebSiteLayer gotoWebSiteLayer(){
        Tab.byText("Веб-сайт").switchTo();
        return new WebSiteLayer();
    }


    @Step("Переход на вкладку 'Объекты' в бакете")
    public ObjectsLayer gotoObjectsLayer(){
        Tab.byText("Объекты").switchTo();
        return new ObjectsLayer();
    }
}
