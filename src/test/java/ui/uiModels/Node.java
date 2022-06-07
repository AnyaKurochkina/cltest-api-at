package ui.uiModels;

import lombok.Getter;
import lombok.Setter;

public class Node {
    @Getter @Setter
    String name, description, input, output, number, timeout, count;

    public Node() {
        this.name = "node_name";
        this.description = "node_description";
        this.number = "";
        this.timeout = "";
        this.count = "";
    }
}
