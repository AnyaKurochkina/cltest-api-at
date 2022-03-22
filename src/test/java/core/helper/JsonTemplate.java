package core.helper;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import core.helper.http.Http;
import org.intellij.lang.annotations.Language;
import org.json.JSONObject;

public class JsonTemplate {
    final JSONObject template;

    public JsonTemplate(JSONObject template) {
        this.template = template;
    }

    public JsonTemplate set(@Language("JSONPath") String s, Object o) {
        if (o != null)
            JsonPath.parse(template).set(s, o);
        return this;
    }

    public JsonTemplate set(@Language("JSONPath") String s, Object o, boolean isReplace) {
        if (isReplace)
            set(s, o);
        else
            JsonPath.parse(template).delete(s);
        return this;
    }

    public JSONObject build() {
        return template;
    }

    public Http send(String url) {
        return new Http(url, template);
    }
}
