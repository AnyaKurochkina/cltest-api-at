package clp.core.iso.core.parse;


import org.jpos.iso.ISODate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import clp.core.iso.core.requests.ProcessingRequest;

import java.util.Date;
import java.util.Map;

public class Int114XMLHandler
extends DefaultHandler {
    ProcessingRequest builtRequest = new ProcessingRequest(false);
    String currentField;
    String transactionCode = "XXXYYZZ";
    boolean setRandomRRN = false;
    boolean setTime = false;

    public Int114XMLHandler() {
        this(false, false);
    }

    public Int114XMLHandler(boolean setRandomRRN, boolean setTime) {
        this.setRandomRRN = setRandomRRN;
        this.setTime = setTime;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        this.currentField = attributes.getValue("name").toLowerCase();
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        this.currentField = "";
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        String value = new String(ch, start, length);
        if (this.currentField.equals("message_type")) {
            this.builtRequest.setMTI(value);
            return;
        }
        if (this.currentField.equals("transaction_code")) {
            this.transactionCode = this.transactionCode.replace("XXX", value);
            return;
        }
        if (this.currentField.equals("from_account_type")) {
            this.transactionCode = this.transactionCode.replace("YY", value);
            return;
        }
        if (this.currentField.equals("to_account_type")) {
            this.transactionCode = this.transactionCode.replace("ZZ", value);
            return;
        }
        if (this.currentField.equals("icc_system_data")) {
            return;
        }
        if ((this.currentField.equals("regional_listing_data")) || (this.currentField.equals("misc_transaction_attributes"))) {
            this.builtRequest.setField(FieldsMap.getISOField(this.currentField), convertSeparators(value));
            return;
        }
        if (FieldsMap.hasField(this.currentField)) {
            this.builtRequest.setField(FieldsMap.getISOField(this.currentField), value);
            return;
        }
    }

    static String convertSeparators(String fieldValue) {
        String result = fieldValue;

        String[] sepList = {"0A", "0a", "10", "1D", "1d", "1C", "1c", "7", "13", "07", "A", "a"};
        char[] sepListConv = {'\n', '\n', '\020', '\035', '\035', '\034', '\034', '\007', '\023', '\007', '\n', '\n'};
        for (int i = 0; i < sepList.length; i++) {
            result = result.replace("\\" + sepList[i],
                    String.valueOf(sepListConv[i]));
        }
        return result;
    }

    public ProcessingRequest getResult() {
        this.builtRequest.setField("3", this.transactionCode);
        if (this.setRandomRRN) {
            Date date = new Date();
            String currentRRN = this.builtRequest.getField("37");
            String newRRN = new StringBuilder(String.valueOf(date.getTime())).reverse()
                    .toString().substring(0, 5);
            this.builtRequest.setField("37", newRRN);
            if ((currentRRN != null) && (!currentRRN.isEmpty())) {
                for (Map.Entry<String, String> entry : this.builtRequest.getFields().entrySet()) {
                    String value = entry.getValue();
                    if (value.contains(currentRRN)) {
                        this.builtRequest.setField(entry.getKey(), value.replace(currentRRN, newRRN));
                    }
                }
            }
        }
        if (this.setTime) {
            Date date = new Date();
            this.builtRequest.setField("7", ISODate.getDateTime(date));
            this.builtRequest.setField("12", ISODate.getTime(date));
            this.builtRequest.setField("13", ISODate.getDate(date));
        }
        return this.builtRequest;
    }
}
