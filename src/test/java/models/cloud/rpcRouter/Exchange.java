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
public class Exchange {
    private String name;
    private String title;
    private String description;
    private ExchangeType exchange_type;
    private Object params;

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("rpcDjangoRouter/createExchange.json")
                .set("$.exchange_type", exchange_type)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.params", params)
                .build();
    }
}
