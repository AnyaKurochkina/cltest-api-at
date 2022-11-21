package ui.models;

import lombok.Getter;
import lombok.Setter;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;

public class Graph {
    @Getter @Setter
    String name, title, version, type, description, author;

    public Graph(String name) {
        this.name = name;
        this.title = "AT UI Graph";
        this.type = GraphType.CREATING.getValue();
        this.version = "1.0.0";
        this.description = "Description";
        this.author = "QA";
    }

    public Graph(String name, String title, GraphType type, String version, String description, String author) {
        this.name = name;
        this.title = title;
        this.type = type.getValue();
        this.description = description;
        this.author = author;
        this.version = version;
    }

    public Graph(String name, String title, String version) {
        this.name = name;
        this.title = title;
        this.type = GraphType.CREATING.getValue();
        this.version = version;
        this.description = "Description";
        this.author = "QA";
    }
}
