package ui.uiModels;

import lombok.Getter;
import lombok.Setter;

public class SubgraphNode extends Node {
    @Getter @Setter
    String subgraphName, subgraphVersion;

    public SubgraphNode(String subgraphName) {
        this.subgraphName = subgraphName;
        this.subgraphVersion = "Последняя";
        this.inputValue = "test_value_1";
        this.input = "{\"input_param\":\""+inputValue+"\"}";
        this.output = "{\"output_param\":\"test_value_2\"}";
    }
}
