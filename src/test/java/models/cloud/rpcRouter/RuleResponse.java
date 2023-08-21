package models.cloud.rpcRouter;

import lombok.*;
import models.AbstractEntity;

import java.util.Date;

import static steps.rpcRouter.RuleSteps.deleteRuleById;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"update_dt", "create_dt"}, callSuper = false)
@ToString
public class RuleResponse extends AbstractEntity {

    private String name;
    private String title;
    private String description;
    private String code_expression;
    private Integer id;
    private Date create_dt;
    private Date update_dt;

    @Override
    protected int getPriority() {
        return 0;
    }

    @Override
    public void delete() {
        deleteRuleById(id).assertStatus(204);
    }
}