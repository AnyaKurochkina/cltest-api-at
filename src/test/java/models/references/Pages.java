package models.references;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.List;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Pages {

    private String name;
    private String id;
    private JSONObject data;
    private String directory;
    private List<String> tags;
    private List<String> ctx_whitelist;
    private List<String> ctx_blacklist;
    private Integer weight;
}
