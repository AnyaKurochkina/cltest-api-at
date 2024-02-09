package api.cloud.productCatalog.action;

import api.Tests;
import models.cloud.productCatalog.action.Action;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;

import java.util.Collections;

@DisabledIfEnv("prod")
@Tags({@Tag("product_catalog"), @Tag("actions")})
public class ActionBaseTest extends Tests {

    public Action createActionModel(String name) {
        Action action = Action.builder()
                .iconUrl("")
                .skipOnPrebilling(false)
                .versionList(Collections.singletonList("1.0.0"))
                .version("1.0.0")
                .lastVersion("1.0.0")
                .number(50)
                .currentVersion("")
                .priority(0)
                .locationRestriction(null)
                .description("desc")
                .isOnlyForApi(true)
                .isDelayable(false)
                .availableWithCostReduction(true)
                .build();
        action.setName(name);
        return action;
    }
}
