package ui.uiModels;

import lombok.Getter;
import lombok.Setter;

public class TemplateNode extends Node {
    @Getter @Setter
    String templateName, templateVersion;

    public TemplateNode(String templateName) {
        this.templateName = templateName;
        this.templateVersion = "Последняя";
        this.timeout = "100";
    }
}
