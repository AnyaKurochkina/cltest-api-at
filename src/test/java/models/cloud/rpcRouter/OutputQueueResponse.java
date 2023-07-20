package models.cloud.rpcRouter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import models.AbstractEntity;

import java.util.Date;

import static steps.rpcRouter.OutputQueueSteps.deleteOutPutQueue;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"update_dt", "create_dt"}, callSuper = false)
public class OutputQueueResponse extends AbstractEntity {
    private Integer id;
    private Integer exchange;
    @JsonIgnore
    private Integer exchange_id;
    private Date create_dt;
    private Date update_dt;
    private String name;
    private String title;
    private String description;
    private Object params;

    @Override
    public void delete() {
        deleteOutPutQueue(id);
    }
}
