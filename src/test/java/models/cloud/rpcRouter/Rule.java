package models.cloud.rpcRouter;

import core.helper.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

    private String name;
    private String title;
    private String description;
    private String code_expression;


    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("rpcDjangoRouter/createRule.json")
                .set("$.code_expression", code_expression)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .build();
    }
}