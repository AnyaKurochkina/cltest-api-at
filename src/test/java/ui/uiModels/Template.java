package ui.uiModels;

import lombok.Getter;
import lombok.Setter;

public class Template {
    @Getter
    @Setter
    String name, title, version, description, runQueue, rollbackQueue, type;

    public Template(String name) {
        this.name = name;
        this.title = "AT UI Template";
        this.type = "system_nodes";
        this.version = "1.0.0";
        this.description = "Description";
        this.runQueue = "internal";
        this.rollbackQueue = "";
    }

    public Template(String name, String title, String runQueue, String rollbackQueue, String type) {
        this.name = name;
        this.title = title;
        this.runQueue = runQueue;
        this.rollbackQueue = rollbackQueue;
        this.type = type;
        this.version = "1.0.0";
    }
}
