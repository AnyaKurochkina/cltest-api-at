package clp.core.reporting;

import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import clp.core.helpers.XmlComparator;



public class AllureReport implements Report {

    @Step("{0}")
    public void logStep(String message) {
        //Empty method for Allure logging
    }


    public void logText(String name, String message) {
        saveTextLog(name, message);
    }


    public void logXml(String name, String xml) {
        saveXmlLog(name, xml);
    }


    public void log(XmlComparator xmlComparator) {
        if (xmlComparator != null) {
            logXml("Received xml", xmlComparator.getTestXml().toString());
            logXml("Expected xml", xmlComparator.getControlXml().toString());
            logText("Differences", xmlComparator.getDifferences());
        }
    }

    @Attachment(value = "{0}", type = "text/plain")
    public String saveTextLog(String attachName, String message) {
        return message == null ? "" : message;
    }

    @Attachment(value = "{0}", type = "text/xml")
    public String saveXmlLog(String attachName, String message) {
        return message == null ? "" : message;
    }

}
