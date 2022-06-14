package ui.uiModels;

import lombok.Getter;
import lombok.Setter;

public class SubgraphNode {
    @Getter @Setter
    String name, description, subgraphName, input, output, number, timeout, count;

    public SubgraphNode(String subgraphName) {
        this.name = "node_name";
        this.description = "node_description";
        this.subgraphName = subgraphName;
        this.input = "{\"in_param\":\"test_value\"}";
        this.output = "{}";
        this.number = "";
        this.timeout = "";
        this.count = "";
    }
}
