package tests.productCatalog;

import org.junit.jupiter.api.Test;
import steps.productCatalog.GraphSteps;

public class GraphTest {

    @Test
    public void createGraph(){
        GraphSteps graphSteps = new GraphSteps();
        graphSteps.createGraph("AtTestGraph");
    }
}
