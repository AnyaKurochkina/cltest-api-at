package ui.uiSteps;

import io.qameta.allure.Step;
import ui.pages.OrgStructurePage;

public class OrgStructureSteps {

    @Step("Выбор организации с именем: {nameOfGlobalOrg}")
    public OrgStructureSteps chooseGlobalOrganization(String nameOfGlobalOrg){
        OrgStructurePage orgStructurePage = new OrgStructurePage();
        orgStructurePage.chooseOrganization(nameOfGlobalOrg);
        return this;
    }

     @Step("Выбор дефолтной организации организации")
    public OrgStructureSteps chooseGlobalOrganization(){
        OrgStructurePage orgStructurePage = new OrgStructurePage();
        orgStructurePage.chooseOrganization();
        return this;
    }

    @Step("Выбор проекта с именем: {nameOfProject}")
    public OrgStructureSteps chooseProject(String nameOfProject){
        OrgStructurePage orgStructurePage = new OrgStructurePage();
        orgStructurePage.chooseProject(nameOfProject);
        return this;
    }

   @Step("Выбор дефолтного проекта")
    public OrgStructureSteps chooseProject(){
        OrgStructurePage orgStructurePage = new OrgStructurePage();
        orgStructurePage.chooseProject();
        return this;
    }
}
