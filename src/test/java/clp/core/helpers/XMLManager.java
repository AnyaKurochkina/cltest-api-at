package clp.core.helpers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import clp.core.exception.CustomException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 */
public class XMLManager {

    private static volatile XMLManager instance;
    private static final Logger LOG = LoggerFactory.getLogger(XMLManager.class);
    private static List<Node> xpathNodesArray;

    private XMLManager() {
    }

    public static XMLManager getInstance(){
        if (instance == null)
            synchronized (XMLManager.class) {
                instance = new XMLManager();
            }
        return instance;
    }

    /**
     * Thread-safe instance creator
     *
     * @return instance of object
     */

    /**
     * @param xmlString
     * @param xPath
     * @return String with node value
     */
    public String getNodeValueForXmlString(String xmlString, String xPath) throws CustomException {
        String nodeValue = null;
        List<Node> nodes = getNodeListByXPath(xmlString, ImmutableList.of(xPath));
        if (nodes.iterator().hasNext()) {
            nodeValue = nodes.iterator().next().getTextContent();
        }
        return nodeValue;
    }

    /**
     * Возвращает список нод, удовлетворяющие xpathStr текущего xml файла
     *
     * @param xmlStr       xml файл в виде строки
     * @param xpathStrList список xpath
     * @return список нод
     */
    private static List<Node> getNodeListByXPath(String xmlStr, List<String> xpathStrList) {
        Document document = null;
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlStr));
            document = builder.parse(inputSource);
            document.getDocumentElement().normalize();
        } catch (Exception ex) {
            LOG.error("", ex);
        }

        List<Node> result = new ArrayList<>();
        if (document != null) {
            XPath xpath = XPathFactory.newInstance().newXPath();
            for (String xpathStr : xpathStrList) {
                NodeList nodes = null;

                try {
                    XPathExpression expr = xpath.compile(xpathStr);
                    nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
                } catch (XPathExpressionException ex) {
                    LOG.error("Can't get NodeList by xpath: " + xpathStr, ex);
                }

                if (nodes != null) {
                    for (int i = 0; i < nodes.getLength(); i++) {
                        result.add(nodes.item(i));
                    }
                }
            }
        }

        return result;
    }

    /**
     * Returns map where:
     * key: filename
     * value: xml data
     */
    public Map<String, String> getTextFiles(URL url) throws CustomException {
        Map<String, String> resultHashMap = new HashMap<>();
        if (url == null) {
            return resultHashMap;
        }

        File folder = FileManager.getFileFromURL(url);
        if (folder == null) {
            LOG.error("Can't found folder for files using url = {}", url);
            return null;
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.exists() && file.isFile()) {
                    String xmlFileString = FileManager.getTextFileAsString(file);
                    resultHashMap.put(file.getName(), xmlFileString);
                }
            }
        }
        return resultHashMap;
    }

    /**
     * Map with:
     * key = fileName + prefix + fileExtension
     * value = [data]
     * <p>
     * Example:
     * File: TestKey_xml1.xml
     * Key: TestKey
     * MapKey:_xml1
     */
    public Map<String, String> getMessageFilesMap(URL url, String prefix) throws CustomException {
        Map<String, String> resultHashMap = new HashMap<>();
        Map<String, String> filesHashMap = getTextFiles(url);
        if (filesHashMap != null) {
            LOG.trace("\nXml keys loading...");
            Pattern pattern = Pattern.compile("^" + Pattern.quote(prefix) + "(.*?).\\w+$");
            for (Map.Entry<String, String> file : filesHashMap.entrySet()) {
                Matcher match = pattern.matcher(file.getKey());
                if (match.find()) {
                    String key = StringUtils.defaultIfBlank(match.group(1), "");
                    LOG.trace("Filename key = {} \nResult key string = {}", file.getKey(), key);
                    resultHashMap.put(key, file.getValue());
                } else {
                    LOG.trace("Warning! Check name of file! Prefix = {} File name = {}", prefix , file.getKey());
                }
            }
        } else {
            LOG.error("Can't create keys dictionary for url = {}", url);
        }
        return resultHashMap;
    }

    public static boolean validateXmlAgainstXSD(InputStream xml, InputStream xsd) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            return true;
        } catch (Exception ex) {
            LOG.error(ex.getLocalizedMessage(), ex);
            return false;
        }
    }

    public static String getDecodedBase64XmlAsString(String encodedBase64XMlString, Charset charset) throws CustomException {
        String decodedXmlString = encodedBase64XMlString;
        if (encodedBase64XMlString == null) {
            return null;
        }

        Document xmlDocument = null;

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            InputStream xmlInputStream = new ByteArrayInputStream(encodedBase64XMlString.getBytes(StandardCharsets.UTF_8));

            LOG.debug("Try to parse received xml file for charset = UTF-8.");
            xmlDocument = docBuilder.parse(new InputSource(new InputStreamReader(xmlInputStream, StandardCharsets.UTF_8)));
            List<XMLNode> nodesArray = XMLManager.getNodesArray(xmlDocument);
            for (XMLNode esbxmlNode : nodesArray) {
                String nodeValue = esbxmlNode.nodeValue;
                byte[] barr = Base64.getDecoder().decode(nodeValue);
                String decodedString = new String(barr, charset);
                if (!decodedString.isEmpty()) {
                    decodedXmlString = decodedXmlString.replace(nodeValue, decodedString);
                }
            }
        } catch (Exception ex) {
            LOG.error("Error parsing! Can't parse encoded in base64 received xml. {}", ex);
            throw new CustomException(ex);
        }

        decodedXmlString = decodedXmlString.replace("<?xml version=\"1.0\" encoding=\"Windows-1251\"?>", "");
        return decodedXmlString;
    }

    public static List<XMLNode> getNodesArray(Document document) {
        List<XMLNode> nodeArrayList = new ArrayList<>();

        NodeList nodeList = document.getElementsByTagName("*");
        XMLNode esbxmlNode = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                esbxmlNode = new XMLNode();
                esbxmlNode.nodeName = node.getNodeName();
                Node firstChild = node.getFirstChild();
                if (firstChild != null && firstChild.getNodeType() == Node.TEXT_NODE) {
                    esbxmlNode.nodeValue = node.getFirstChild().getTextContent();
                }

                Map<String, String> attributesHashMap = new HashMap<>();
                if (node.hasAttributes()) {
                    NamedNodeMap attributesMap = node.getAttributes();
                    for (Integer j = 0; j < attributesMap.getLength(); j++) {
                        Attr attribute = (Attr) attributesMap.item(j);
                        if (attribute != null) {
                            attributesHashMap.put(attribute.getNodeName(), attribute.getNodeValue());
                        }
                    }
                }
                esbxmlNode.attributes = attributesHashMap;
                xpathNodesArray = new ArrayList();
                getArrayOfParentNodes(node);
                if (xpathNodesArray != null && !xpathNodesArray.isEmpty()) {
                    String xpath = "";
                    for (int it = xpathNodesArray.size() - 1; it >= 0; it--) {
                        Node currentNode = (Node) xpathNodesArray.get(it);
                        xpath = xpath + "/" + currentNode.getNodeName();
                    }
                    esbxmlNode.xpathString = xpath;
                    xpathNodesArray = null;
                }

            }
            nodeArrayList.add(esbxmlNode);
        }
        return nodeArrayList;
    }

    private static Node getArrayOfParentNodes(Node node) {
        boolean hasParent = node.getParentNode() != null;
        if (hasParent) {
            xpathNodesArray.add(node.getParentNode());
            getArrayOfParentNodes(node.getParentNode());
            return node.getParentNode();

        } else {
            return null;
        }
    }
}



