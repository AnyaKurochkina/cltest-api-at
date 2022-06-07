package ui.uiModels;

import lombok.Getter;
import lombok.Setter;

public class SubgraphNode extends Node {
    @Getter @Setter
    String subgraphName, subgraphVersion;

    public SubgraphNode(String subgraphName) {
        this.subgraphName = subgraphName;
        this.input = "{\"in_param\":\"test_value\"}";
        this.output = "{}";
    }
}
