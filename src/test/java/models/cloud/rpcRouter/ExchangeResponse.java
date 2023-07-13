package models.cloud.rpcRouter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import models.AbstractEntity;

import static steps.rpcRouter.ExchangeSteps.deleteExchange;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class ExchangeResponse extends AbstractEntity {
    private String name;
    private String title;
    private String description;
    private ExchangeType exchange_type;
    private Object params;
    private Integer id;
    private String create_dt;
    private String update_dt;

    @Override
    public void delete() {
        deleteExchange(id);
    }
}
