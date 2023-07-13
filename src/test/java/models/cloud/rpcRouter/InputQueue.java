package models.cloud.rpcRouter;

import core.helper.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.List;
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InputQueue{

	private Boolean durable;
	private String name;
	private String description;
	private List<Integer> rules;
	private String title;
	private Object params;

	public JSONObject toJson() {
		return JsonHelper.getJsonTemplate("rpcDjangoRouter/createInputQueue.json")
				.set("$.durable", durable)
				.set("$.name", name)
				.set("$.title", title)
				.set("$.description", description)
				.set("$.params", params)
				.set("$.rules", rules)
				.build();
	}
}