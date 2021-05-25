package clp.core.helpers;


import java.util.Map;

/**
 * 
 */
public class XMLNode {
    String nodeName;
    String nodeValue;
    String xpathString;
    Map<String, String> attributes;

    @Override
    public String toString() {
        StringBuilder attributeBuilder = new StringBuilder();
        attributeBuilder.append("{ ");
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            attributeBuilder.append("[").append(entry.getKey()).append(", ").append(entry.getValue()).append("]");
        }
        attributeBuilder.append(" }");
        return "Comparing node:" +
                "\n --> Received node: " + nodeName +
                "\n --> Received node value: " + nodeValue +
                "\n --> Xpath:" + xpathString +
                "\n --> Received node attributes: " + attributeBuilder.toString();
    }

}
