package models.cloud.rpcRouter;

import lombok.*;
import models.AbstractEntity;

import static steps.rpcRouter.ExchangeSteps.deleteExchange;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"update_dt", "create_dt"}, callSuper = false)
@ToString
public class ExchangeResponse extends AbstractEntity {
    private String name;
    private String title;
    private String description;
    private ExchangeType exchange_type;
    private Object params;
    private Integer id;
    private String create_dt;
    private String update_dt;
    private String queue_list;

    @Override
    protected int getPriority() {
        return 0;
    }

    @Override
    public void delete() {
        deleteExchange(id).assertStatus(204);
    }
}
