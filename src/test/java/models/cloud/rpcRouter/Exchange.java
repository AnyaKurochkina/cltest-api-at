package models.cloud.rpcRouter;

import core.helper.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.AbstractEntity;
import org.json.JSONObject;

import java.util.Objects;

import static steps.rpcRouter.ExchangeSteps.deleteExchange;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"create_dt", "update_dt", "id"}, callSuper = false)
public class Exchange extends AbstractEntity {
    private String name;
    private String title;
    private String description;
    private ExchangeType exchange_type;
    private Objects params;
    private Integer id;
    private String create_dt;
    private String update_dt;

    @Override
    public void delete() {
        deleteExchange(id);
    }

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("rpcDjangoRouter/createOutPutQueue.json")
                .set("$.exchange_type", exchange_type)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.params", params)
                .build();
    }

    enum ExchangeType {
        DIRECT("direct"),
        TOPIC("topic"),
        FANOUT("fanout");

        private final String value;

        ExchangeType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
