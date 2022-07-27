package ui.uiModels;

import lombok.Getter;
import lombok.Setter;

public class Graph {
    @Getter @Setter
    String description, author;

    public Graph() {
        this.description = "Description";
        this.author = "QA";
    }

    public Graph(String description, String author) {
        this.description = description;
        this.author = author;
    }
}
