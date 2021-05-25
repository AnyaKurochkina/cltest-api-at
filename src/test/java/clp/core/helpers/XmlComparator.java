package clp.core.helpers;


import org.custommonkey.xmlunit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import clp.core.exception.CustomException;
import clp.core.listeners.IgnoringRuleDifferenceListener;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlComparator {
    private static final Logger LOG = LoggerFactory.getLogger(XmlComparator.class);
    private final XmlUtil testXml;
    private final XmlUtil controlXml;
    private Boolean isSimilar = false;
    private List<String> ignoringXPathList = new ArrayList<>();
    private List<Difference> differenceList = new ArrayList<>();

    private XmlComparator(XmlUtil testXml, XmlUtil controlXml, List<String> ignoringXPathList) throws CustomException, SAXException {
        this.testXml = testXml;
        this.controlXml = controlXml;
        if (ignoringXPathList != null) {
            this.ignoringXPathList = ignoringXPathList;
        }
        compareXmls();
    }

    public static XmlComparator create(XmlUtil testXml, XmlUtil controlXml, List<String> ignoringXPathList) throws CustomException, SAXException {
        return new XmlComparator(testXml, controlXml, ignoringXPathList);
    }

    public static XmlComparator create(XmlUtil testXml, XmlUtil controlXml) throws CustomException, SAXException {
        return create(testXml, controlXml, null);
    }

    public boolean isXmlsSimilar() {
        return isSimilar;
    }

    public String getDifferences() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Difference difference : differenceList) {
            stringBuilder.append(difference.toString()).append("\n").append("\n");
        }
        return stringBuilder.toString();
    }

    public XmlUtil getTestXml() {
        return testXml;
    }

    public XmlUtil getControlXml() {
        return controlXml;
    }

    private void compareXmls() throws CustomException, org.xml.sax.SAXException {
        LOG.debug("Start XML comparing...");

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        Diff diff = null;
        try {
            for (String xpath : ignoringXPathList) {
                testXml.removeNodesByXPath(xpath);
                controlXml.removeNodesByXPath(xpath);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Received (test) XML is:\n{}", testXml != null ? testXml.toString() : "null");
                LOG.debug("Expected (control) XML is:\n{}", controlXml != null ? controlXml.toString() : "null");
            }
            if (testXml != null && controlXml != null) {
                diff = new Diff(controlXml.toString(), testXml.toString());
                diff.overrideDifferenceListener(new IgnoringRuleDifferenceListener());
                diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
                isSimilar = diff.similar();
                if (!isSimilar) {
                    DetailedDiff detDiff = new DetailedDiff(diff);
                    List differences = detDiff.getAllDifferences();
                    for (Object obj : differences) {
                        differenceList.add((Difference) obj);
                    }
                }
            }

            if (LOG.isDebugEnabled() && diff != null && !isSimilar) {
                for (Difference difference : differenceList) {
                    LOG.debug(difference.toString());
                }
                LOG.debug("End of XML comparing. Count difference is '{}'", differenceList.size());
            }
        } catch (XPathExpressionException | IOException e) {
            LOG.error("Error parsing!", e);
            throw new CustomException(e);
        }
    }
}
