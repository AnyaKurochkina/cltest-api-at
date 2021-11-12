package tests.productCatalog;

import org.junit.jupiter.api.Test;
import steps.productCatalog.GraphSteps;

public class ActionsTest {

    @Test
    public void test(){
        GraphSteps createGraphResponse = new GraphSteps();
        String graphId = createGraphResponse.getGraph("AtTestGraph");
    }
}
