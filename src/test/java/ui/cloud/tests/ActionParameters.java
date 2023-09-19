package ui.cloud.tests;

import com.codeborne.selenide.SelenideElement;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Builder
@Data
public class ActionParameters {
    @Builder.Default
    boolean isSimpleAction = false;
    @Builder.Default
    boolean clickCancel = false;
    @Builder.Default
    boolean checkPreBilling = true;
    @Builder.Default
    boolean checkLastAction = true;
    @Builder.Default
    boolean checkAlert = true;
    @Builder.Default
    boolean waitChangeStatus = true;
    Duration timeOut;
    SelenideElement node;
}
