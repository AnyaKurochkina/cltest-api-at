package ui.uiModels;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GraphModifier {

    String name, number, schema, type, path, modifierData, modifierDataSubstring;
    String[] envs;

    public GraphModifier(String name) {
        this.name = name;
        this.envs = new String[]{"dev"};
        this.number = "1";
        this.schema = "json_schema";
        this.type = "replace";
        this.path = "";
        this.modifierData = "";
        this.modifierDataSubstring = "";
    }
}
