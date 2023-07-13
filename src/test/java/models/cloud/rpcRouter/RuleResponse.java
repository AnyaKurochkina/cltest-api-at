package models.cloud.rpcRouter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import models.AbstractEntity;

import java.util.Date;

import static steps.rpcRouter.RuleSteps.deleteRuleById;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RuleResponse extends AbstractEntity {

    private String name;
    private String title;
    private String description;
    private String code_expression;
    private Integer id;
    private Date create_dt;
    private Date update_dt;

    @Override
    public void delete() {
        deleteRuleById(id).assertStatus(204);
    }
}