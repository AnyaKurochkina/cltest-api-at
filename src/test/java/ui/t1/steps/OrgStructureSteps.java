package ui.t1.steps;

import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import ui.t1.pages.OrgStructurePage;

@Log4j2
public class OrgStructureSteps {

    @Step("Выбор организации с именем: {nameOfGlobalOrg}")
    public OrgStructureSteps chooseGlobalOrganization(String nameOfGlobalOrg) {
        OrgStructurePage orgStructurePage = new OrgStructurePage();
        orgStructurePage.chooseOrganization(nameOfGlobalOrg);
        return this;
    }

    @Step("Выбор дефолтной организации")
    public OrgStructureSteps chooseGlobalOrganization() {
        OrgStructurePage orgStructurePage = new OrgStructurePage();
        orgStructurePage.chooseOrganization();
        return this;
    }

    @Step("Выбор проекта с именем: {nameOfProject}")
    public OrgStructureSteps chooseProject(String nameOfProject) {
        OrgStructurePage orgStructurePage = new OrgStructurePage();
        orgStructurePage.chooseProject(nameOfProject);
        return this;
    }

    @Step("Выбор дефолтного проекта")
    public OrgStructureSteps chooseProject() {
        OrgStructurePage orgStructurePage = new OrgStructurePage();
        orgStructurePage.chooseProject();
        return this;
    }
}
