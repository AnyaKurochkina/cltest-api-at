package models.cloud.rpcRouter;

import core.helper.JsonHelper;
import lombok.*;
import models.AbstractEntity;
import org.json.JSONObject;

import static steps.rpcRouter.OutputQueueSteps.deleteOutPutQueue;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"create_dt", "update_dt", "id"}, callSuper = false)
public class OutputQueue extends AbstractEntity {
    private Integer id;
    private Integer exchange;
    private String create_dt;
    private String update_dt;
    private String name;
    private String title;
    private String description;
    private Object params;

    @Override
    public void delete() {
            deleteOutPutQueue(id);
    }

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
