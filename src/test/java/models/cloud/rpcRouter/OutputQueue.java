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
public class OutputQueue {

    private Integer exchange;
    private String name;
    private String title;
    private String description;
    private Object params;

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("rpcDjangoRouter/createOutPutQueue.json")
                .set("$.exchange", exchange)
                .set("$.name", name)
                .set("$.title", title)
                .set("$.description", description)
                .set("$.params", params)
                .build();
    }
}
