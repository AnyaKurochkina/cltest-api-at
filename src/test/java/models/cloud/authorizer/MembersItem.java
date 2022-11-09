package models.cloud.authorizer;

import lombok.Setter;

import java.util.List;

@Setter
public class MembersItem{
	private Resource resource;
	private List<RolesItem> roles;
}