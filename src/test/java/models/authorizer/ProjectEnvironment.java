package models.authorizer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import models.Entity;
import models.EntityOld;

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
