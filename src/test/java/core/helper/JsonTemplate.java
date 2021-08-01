package core.helper;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import org.json.JSONObject;

public class JsonTemplate {
    JSONObject template;

    public JsonTemplate(JSONObject template) {
        this.template = template;
    }

    public JsonTemplate set(String s, Object o, Predicate... p) {
        JsonPath.parse(template).set(s, o, p);
        return this;
    }

    public JSONObject build() {
        return template;
    }

    public Http send(String url) {
        return new Http(url, template);
    }
}
