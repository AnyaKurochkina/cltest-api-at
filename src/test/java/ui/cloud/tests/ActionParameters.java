package ui.cloud.tests;

import com.codeborne.selenide.SelenideElement;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ActionParameters {
    @Builder.Default
    boolean checkPreBilling = true;
    @Builder.Default
    boolean checkLastAction = true;
    @Builder.Default
    boolean checkAlert = true;
    @Builder.Default
    boolean waitChangeStatus = true;
    SelenideElement node;
}
