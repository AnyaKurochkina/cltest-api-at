package models.cloud.rpcRouter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.AbstractEntity;

import static steps.rpcRouter.OutputQueueSteps.deleteOutPutQueue;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"create_dt", "update_dt", "id"}, callSuper = false)
public class OutputQueue extends AbstractEntity {
    private Integer id;
    private Integer exchange;
    @JsonIgnore
    private String create_dt;
    @JsonIgnore
    private String update_dt;
    private String name;
    private String title;
    private String description;
    private Object params;

    @Override
    public void delete() {
        deleteOutPutQueue(id);
    }
}
