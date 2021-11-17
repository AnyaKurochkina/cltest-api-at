package models.orderService;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
//py -m pip install testit-allure-adaptor
// py -m pip install --upgrade certifi
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class ResourcePool {
    public String id;
    @Getter
    public String label;
    public String name;
    public String projectId;

    @Override
    public String toString() {
        return String.format("{\"id\": \"%s\", \"name\": \"%s\"}", id, name);
    }


}
