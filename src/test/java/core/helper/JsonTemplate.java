package core.helper;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import org.intellij.lang.annotations.Language;
import org.json.JSONObject;

public class JsonTemplate {
    JSONObject template;

    public JsonTemplate(JSONObject template) {
        this.template = template;
    }

    public JsonTemplate set(@Language("JSONPath") String s, Object o, Predicate... p) {
        if (o != null)
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
