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
    private String code_expression_result;
    private Object code_expression_errors;
    private String test_queue_name;
    private Object test_code_params;


    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("rpcDjangoRouter/createRule.json")
                .set("$.code_expression", code_expression)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.code_expression_result", code_expression_result)
                .set("$.code_expression_errors", code_expression_errors)
                .set("$.test_queue_name", test_queue_name)
                .set("$.test_code_params", test_code_params)
                .build();
    }
}