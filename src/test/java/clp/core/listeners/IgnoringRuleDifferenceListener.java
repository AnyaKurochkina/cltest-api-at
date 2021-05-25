package clp.core.listeners;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.NodeDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import clp.core.enums.ParameterType;


public class IgnoringRuleDifferenceListener implements DifferenceListener {
    private static final Logger LOG = LoggerFactory.getLogger(IgnoringRuleDifferenceListener.class);

    public IgnoringRuleDifferenceListener() {
    }

    @Override
    public int differenceFound(Difference difference) {
        NodeDetail testNodeDetail = difference.getTestNodeDetail();
        NodeDetail controlNodeDetail = difference.getControlNodeDetail();

        if (LOG.isTraceEnabled()) {
            LOG.trace("---------- DIFFERENCE ------------------------------");
            LOG.trace("--- Listener#differenceFount: {}", difference);
            LOG.trace("--- Test node detail: {}", convertNodeDetailToString(difference.getTestNodeDetail()));
            LOG.trace("--- Control node detail: {}", convertNodeDetailToString(difference.getControlNodeDetail()));
        }

        if (testNodeDetail.getValue().equalsIgnoreCase(ParameterType.IGNORED.getParameter()) ||
                controlNodeDetail.getValue().equalsIgnoreCase(ParameterType.IGNORED.getParameter())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("---------- IGNORED DIFFERENCE ------------------------------");
                LOG.debug("--- Listener#differenceFount: {}", difference);
                LOG.debug("--- Test node detail: {}", convertNodeDetailToString(difference.getTestNodeDetail()));
                LOG.debug("--- Control node detail: {}", convertNodeDetailToString(difference.getControlNodeDetail()));
            }
            return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
        }

        if (difference.equals(DifferenceConstants.CHILD_NODELIST_SEQUENCE)) {
            return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
        }

        return RETURN_ACCEPT_DIFFERENCE;
    }

    @Override
    public void skippedComparison(Node control, Node test) {

    }

    private String convertNodeDetailToString(NodeDetail nodeDetail) {
        if (nodeDetail == null) {
            return "null";
        }
        return "value: " + nodeDetail.getValue() + ", " +
                "node: " + convertNodeToString(nodeDetail.getNode()) + ", " +
                "xpath: " + nodeDetail.getXpathLocation();
    }

    private String convertNodeToString(Node node) {
        if (node == null) {
            return "[null]";
        }
        return "[nodeName: " + node.getNodeName() + ", " +
                "nodeValue: " + node.getNodeValue() + ", " +
                "nodeType: " + node.getNodeType() + "]";
    }
}
