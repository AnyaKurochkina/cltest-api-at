package clp.core.reporting;

import clp.core.helpers.XmlComparator;

public interface Report {
    void logStep(String message);

    void logText(String name, String message);

    void log(XmlComparator xmlComparator);

    void logXml(String name, String xml);
}
