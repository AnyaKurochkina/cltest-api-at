package ui.uiModels;

import lombok.Getter;
import lombok.Setter;

public class Node {
    @Getter @Setter
    String name, description, input, inputKey, inputValue, output, outputKey, number, timeout, count;

    public Node() {
        this.name = "node_name";
        this.description = "node_description";
        this.inputKey = "input_param";
        this.outputKey = "output_param";
        this.number = "";
        this.timeout = "";
        this.count = "";
    }
}
