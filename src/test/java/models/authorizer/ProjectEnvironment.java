package models.authorizer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProjectEnvironment  {
    public String id;
    public String envType;
    public String env;

    public ProjectEnvironment(String env){
        this.env = env;
    }
}
