package ui.t1.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.log4j.Log4j2;
import ui.uiInterfaces.Loadable;

import static com.codeborne.selenide.Selenide.*;
import static core.helper.Configure.getAppProp;
@Log4j2
public class OrgStructurePage implements Loadable {
    private static final String defaultProject = getAppProp("default.project");
    private static final String defaultGlobalOrganization = getAppProp("default.global.organization");

    private final SelenideElement showAllProjectsBtn = $x("//button[@class='MuiButtonBase-root MuiIconButton-root MuiIconButton-sizeSmall']");
    private final SelenideElement listOfGlobalOrganizationsInput = $x("//input[@name='organization']");
    private final SelenideElement currentGlobalOrganization = $x("//span[text()='Организация ']/following-sibling::*");
    private final SelenideElement orgStructureTitle = $x("//div[text()='Организационная структура']");
    private final SelenideElement chooseContextBtn = $x("//div[3]/ul/li[4]");
    ElementsCollection tableOfOrgs = $$x("//tbody[@role='rowgroup']//tr");
    String nameOfProjectRow = "./td[1]/div/div[3]/div";
    String idOfProjectRow = "./td[3]/div/div[3]/div";
    String menuOfProjectRow = ".//button[@id='actions-menu-button']";

    public OrgStructurePage() {
        checkPage();
    }

    public OrgStructurePage chooseOrganization(String nameOfGlobalOrg){
        if (listOfGlobalOrganizationsInput.exists()){
            listOfGlobalOrganizationsInput.shouldBe(Condition.enabled).click();
            $x("//div[not(contains(@style,'visibility: hidden;'))]//li[text()='"+ nameOfGlobalOrg +"']")
                    .shouldBe(Condition.enabled)
                    .click();
            listOfGlobalOrganizationsInput.shouldHave(Condition.attribute("value", nameOfGlobalOrg));
        } else {
            log.info("На страницы орг. структуры, всего 1 организация : " + currentGlobalOrganization.shouldBe(Condition.visible).getText());
        }
        return this;
    }

    public OrgStructurePage chooseOrganization(){
        if (listOfGlobalOrganizationsInput.exists()){
        listOfGlobalOrganizationsInput.shouldBe(Condition.enabled).click();
        $x("//div[not(contains(@style,'visibility: hidden;'))]//li[text()='"+ defaultGlobalOrganization +"']")
                .shouldBe(Condition.enabled)
                .click();
        listOfGlobalOrganizationsInput.shouldHave(Condition.attribute("value", defaultGlobalOrganization));
        showAllProjectsBtn.shouldBe(Condition.enabled).click();
        } else {
            log.info("На страницы орг. структуры, всего 1 организация : " + currentGlobalOrganization.shouldBe(Condition.visible).getText());
        }
        return this;
    }

    public void chooseProject(String nameOfProject){
        sleep(2000);
        tableOfOrgs.shouldHave(CollectionCondition.sizeGreaterThan(1));
        for(SelenideElement i: tableOfOrgs){
            if (i.$x(nameOfProjectRow).getText().equals(nameOfProject)){
                i.$x(menuOfProjectRow).scrollIntoView(true).shouldBe(Condition.enabled).click();
                chooseContextBtn.shouldBe(Condition.enabled).click();
                break;
            }
        }
    }

   public void chooseProject(){
        sleep(2000);
        tableOfOrgs.shouldHave(CollectionCondition.sizeGreaterThan(1));
        for(SelenideElement i: tableOfOrgs){
            if (i.$x(nameOfProjectRow).getText().equals(defaultProject)){
                i.$x(menuOfProjectRow).scrollIntoView(true).shouldBe(Condition.enabled).click();
                chooseContextBtn.shouldBe(Condition.enabled).click();
                break;
            }
        }
    }

    @Override
    public void checkPage() {
        orgStructureTitle.shouldBe(Condition.visible);
    }
}
