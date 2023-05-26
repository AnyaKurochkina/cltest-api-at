package models.cloud.tagService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import steps.resourceManager.ResourceManagerSteps;

@Data
@AllArgsConstructor
public class Context {
    private String type;
    private String id;

    public String getContextPath(){
        return ResourceManagerSteps.getProjectPath(type, id) + "/";
    }
    public static Context byId(@NonNull String id){
        String type = "organizations";
            if(id.startsWith("proj"))
                type = "projects";
            else if(id.startsWith("fold"))
                type = "folders";
            return new Context(type, id);
        }
}
