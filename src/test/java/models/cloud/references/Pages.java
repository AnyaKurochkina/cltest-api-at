package models.cloud.references;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Pages {

    private String name;
    private String id;
    private Object data;
    private String directory;
    private List<String> tags;
    private List<String> ctx_whitelist;
    private List<String> ctx_blacklist;
    private Integer weight;
}
