package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;

public class VmPage extends IProductT1Page{

    public VmPage checkCreate(){
        waitChangeStatus();
        checkLastAction("Развертывание");
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        return this;
    }

}
