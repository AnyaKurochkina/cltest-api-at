package api.cloud.productCatalog.graph;

import api.Tests;
import models.cloud.productCatalog.graph.Graph;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Tag;

@DisabledIfEnv("prod")
@Tag("product_catalog")
@Tag("Graphs")
public class GraphBaseTest extends Tests {

    public Graph createGraphModel(String name) {
        return Graph.builder()
                .name(name)
                .build();
    }
}
