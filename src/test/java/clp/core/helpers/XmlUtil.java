package clp.core.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import clp.core.exception.CustomException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class XmlUtil {
    private static final Logger LOG = LoggerFactory.getLogger(XmlUtil.class);
    private final Document document;
    private final NamespaceContext namespaceContext;

    public XmlUtil(String xml, NamespaceContext nc) throws CustomException, IOException, SAXException {
        document = parseXml(xml);
        namespaceContext = nc;
    }

    public static XmlUtil createXml(String xml) throws CustomException {
        try {
            return new XmlUtil(xml, null);
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static XmlUtil createXml(String xml, NamespaceContext nc) throws CustomException {
        try {
            return new XmlUtil(xml, nc);
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public Document getDocument() {
        return document;
    }

    public XmlUtil addNode(String path, String xml) throws CustomException, IOException, SAXException {
        Document subDoc = parseXml(xml);
        Node destNode = null;
        try {
            destNode = findNode(asXPath(path));
        } catch (XPathExpressionException e) {
            throw new CustomException(e);
        }
        if (destNode != null) {
            Node srcNode = subDoc.getFirstChild();
            destNode.appendChild(document.adoptNode(srcNode.cloneNode(true)));
        }
        return this;
    }

    public XmlUtil removeNode(String path) throws XPathExpressionException {
        Node destNode = findNode(asXPath(path));
        if (destNode != null) {
            if (destNode.getNodeType() == Node.ELEMENT_NODE) {
                Node parentNode = destNode.getParentNode();
                if (parentNode != null) {
                    parentNode.removeChild(destNode);
                } else {
                    LOG.error("Error! Parent node of '{}' is empty! Can't remove node.", destNode.getNodeName());
                }
            } else if (destNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                LOG.info("{}", destNode);
            }
        }
        return this;
    }

    public XmlUtil removeNodesByXPath(String path) throws XPathExpressionException {
        List<Node> nodeList = findNodes(document, asXPath(path));
        for (Node node : nodeList) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Node parentNode = node.getParentNode();
                if (parentNode != null) {
                    parentNode.removeChild(node);
                } else {
                    LOG.error("Error! Parent node of '{}' is empty! Can't remove node.", node.getNodeName());
                }
            } else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                String[] pathAttr = path.split(Pattern.quote("/@"));
                if (pathAttr.length == 2) {
                    removeAttribute(pathAttr[0], pathAttr[1]);
                } else {
                    LOG.error("Error! Can't remove attribute '{}' by xpath '{}'", node.getNodeName(), path);
                }
            } else {
                LOG.error("Error! Can't remove node '{}'! Unsupported node type '{}'.", node.getNodeName(), node.getNodeType());
            }
        }
        return this;
    }

    public XmlUtil addAttribute(String path, String attr, String value) throws XPathExpressionException {
        Element destNode = (Element) findNode(asXPath(path));
        if (destNode != null) {
            destNode.setAttribute(attr, value);
        }
        return this;
    }

    public XmlUtil removeAttribute(String path, String attr) throws XPathExpressionException {
        Element destNode = (Element) findNode(asXPath(path));
        if (destNode != null) {
            destNode.removeAttribute(attr);
        }
        return this;
    }

    public String toString() {
        return docToString(document);
    }

    public String docToString(Node doc) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            return null;
        }
    }


    private String getNodeText(Node node) {
        if (node != null) {
            if (node.getNodeType() == Node.TEXT_NODE) {
                return node.getNodeValue();
            } else if (node.getChildNodes() != null) {
                NodeList nodeList = node.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node n = nodeList.item(i);
                    String res = getNodeText(n);
                    if (res != null) {
                        return res;
                    }
                }
            }
        }
        return null;
    }

    private Document parseXml(String xml) throws CustomException, IOException, SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setValidating(false);
        docFactory.setNamespaceAware(false);
        docFactory.setIgnoringComments(true);
        docFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new CustomException(e);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        InputSource inputSource = new InputSource(reader);
        inputSource.setEncoding(StandardCharsets.UTF_8.toString());
        Document parse = docBuilder.parse(inputSource);
        parse.getDocumentElement().normalize();
        return parse;
    }

    private String asXPath(String path) {
        return path.startsWith("/") ? path : "//" + path;
    }

    private Node findNode(String xpathStr) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        if (namespaceContext != null) {
            xpath.setNamespaceContext(namespaceContext);
        }
        XPathExpression expr = xpath.compile(xpathStr);
        Node node = (Node) expr.evaluate(document, XPathConstants.NODE);
        if (node == null) {
            LOG.error("Error! Can't find Node by xpath '{}'", xpathStr);
        }
        return node;
    }

    private List<Node> findNodes(Document doc, String xpathStr) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile(xpathStr);
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        List<Node> result = new ArrayList<>();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                result.add(nodeList.item(i));
            }
        } else {
            LOG.error("Error! Can't find NodeList by xpath '{}'", xpathStr);
        }
        Collections.sort(result, (o1, o2) -> o1.getNodeType() - o2.getNodeType());
        return result;
    }
}
