package models.cloud.rpcRouter;

import lombok.*;
import models.AbstractEntity;

import java.util.Date;
import java.util.List;

import static steps.rpcRouter.InputQueueSteps.deleteInputQueue;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"update_dt", "create_dt"}, callSuper = false)
public class InputQueueResponse extends AbstractEntity {
	private Integer id;
	private Date create_dt;
	private Date update_dt;
	private Boolean durable;
	private String name;
	private String description;
	private List<Integer> rules;
	private String title;
	private Object params;

	@Override
    protected int getPrioritise() {
        return 1;
    }

	@Override
	public void delete() {
		deleteInputQueue(id).assertStatus(204);
	}
}