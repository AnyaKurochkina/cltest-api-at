package models.authorizer;

import lombok.*;
import models.Entity;
import org.json.JSONObject;

import java.util.List;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends Entity {
	private String firstname;
	private String updatedAt;
	private List<MembersItem> members;
	private Boolean active;
	private String createdAt;
	private String id;
	private String email;
	private String username;
	private Object lastname;

	@Override
	public Entity init() {
		return null;
	}

	@Override
	public JSONObject toJson() {
		return null;
	}

	@Override
	protected void create() {

	}

	@Override
	protected void delete() {

	}
}