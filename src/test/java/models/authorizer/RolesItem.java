package models.authorizer;

import lombok.Setter;

import java.util.Date;

@Setter
public class RolesItem{
	private Date updatedAt;
	private String name;
	private String description;
	private Date createdAt;
	private String title;
	private String type;
}
