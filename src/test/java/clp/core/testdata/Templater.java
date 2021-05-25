package clp.core.testdata;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class Templater {
    private String bodyTemplate;
    private Map<String, String> templateMap;


    public Templater(String body, Map<String, String> map) {
        this.bodyTemplate = body;
        this.templateMap = map;

    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public Map<String, String> getTemplateMap() {
        return templateMap;
    }

    public void setTemplateMap(Map<String, String> templateMap) {
        this.templateMap = templateMap;
    }

    public String fillTemplate() throws ParseException {
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        RuntimeServices rts = RuntimeSingleton.getRuntimeServices();
        StringReader sr = new StringReader(this.bodyTemplate);
        SimpleNode sn = rts.parse(sr, "tmpl");
        Template tmpl = new Template();
        tmpl.setRuntimeServices(rts);
        tmpl.setData(sn);
        tmpl.initDocument();
        VelocityContext vc = new VelocityContext();
        for (Map.Entry entry : this.templateMap.entrySet()) {
            vc.put(entry.getKey().toString(), entry.getValue());
        }
        StringWriter sw = new StringWriter();
        tmpl.merge(vc, sw);
        return sw.toString();
    }
}
