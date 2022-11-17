package core.helper;

import com.jayway.jsonpath.JsonPath;
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

    public JsonTemplate put(@Language("JSONPath") String path, String s, Object o) {
        if (o != null)
            JsonPath.parse(template).put(path, s, o);
        return this;
    }

    public JsonTemplate setIfNullRemove(@Language("JSONPath") String s, Object o) {
        if (o == null) {
            JsonPath.parse(template).delete(s);
        } else {
            JsonPath.parse(template).set(s, o);
        }
        return this;
    }

    public JsonTemplate remove(@Language("JSONPath") String s, boolean is) {
        if (is) JsonPath.parse(template).delete(s);
        return this;
    }

    public JsonTemplate remove(@Language("JSONPath") String s) {
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
