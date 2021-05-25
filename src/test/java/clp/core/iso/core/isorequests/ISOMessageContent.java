package clp.core.iso.core.isorequests;

import org.jpos.iso.ISOMsg;

import java.util.*;

public class ISOMessageContent {
    private String                  MTI;
    private String                  ISOHeader;
    private HashMap<String, String> fields;
    private ISOMsg                  sourceMessage;

    public ISOMessageContent(final String ISOHeader, final String MTI, final HashMap<String, String> fields) {
        this.ISOHeader = ISOHeader;
        this.MTI = MTI;
        this.fields = fields;
    }

    public ISOMessageContent() {
        this.fields = new HashMap<>();
    }

    public String getMTI() {
        return this.MTI;
    }

    public void setMTI(final String mTI) {
        this.MTI = mTI;
    }

    public String getISOHeader() {
        return this.ISOHeader;
    }

    public void setISOHeader(final String isoHeader) {
        this.ISOHeader = isoHeader;
    }

    public HashMap<String, String> getFields() {
        return this.fields;
    }

    public void setFields(final HashMap<String, String> fields) {
        this.fields = fields;
    }

    public ISOMsg getSourceMessage() {
        return this.sourceMessage;
    }

    public void setSourceMessage(final ISOMsg sourceMessage) {
        this.sourceMessage = sourceMessage;
    }

    public String toXMLString() {
        final StringBuilder result = new StringBuilder();
        result.append("<fields>");
        for (final Map.Entry<String, String> entry : this.getFields().entrySet()) {
            result.append("<entry>");
            result.append("<key>").append(entry.getKey()).append("</key>");
            result.append("<value>").append(entry.getValue()).append("</value>");
            result.append("</entry>");
        }
        result.append("</fields>");
        result.append("<ISOHeader>").append(this.getISOHeader()).append("</ISOHeader>");
        result.append("<MTI>").append(this.getMTI()).append("</MTI>");
        return result.toString();
    }

    public String getISOFieldsString() {
        final StringBuilder fieldsString = new StringBuilder();
        final HashMap<String, String> responseFields = this.getFields();

        final SortedSet<String> keys = new TreeSet<>(new FieldNumbersComparator());
        keys.addAll(responseFields.keySet());
        for (final String key : keys) {
            fieldsString.append("   ").append(key).append("=").append(responseFields.get(key)).append("\r\n");
        }
        return fieldsString.toString();
    }

    public void setField(final String fieldName, final String fieldValue) {
        this.getFields().put(fieldName, fieldValue);
    }

    public String getField(final String fieldName) {
        return this.getFields().get(fieldName);
    }

    @Override
    public String toString() {
        return "Заголовки: " + System.lineSeparator() + getISOHeader() + System.lineSeparator()
        + "Тело: " + System.lineSeparator() + getISOFieldsString();
    }

    class FieldNumbersComparator implements Comparator<String> {
        FieldNumbersComparator() {}

        @Override
        public int compare(final String field1, final String field2) {
            try {
                final Float field1number = Float.valueOf(field1);
                final Float field2number = Float.valueOf(field2);
                if (field1number.intValue() == field2number.intValue() && field1.contains(".") && field2.contains(".")) {
                    final Integer subfield1number = Integer.valueOf(field1.substring(field1.indexOf(".") + 1));
                    final Integer subfield2number = Integer.valueOf(field2.substring(field2.indexOf(".") + 1));
                    return subfield1number.compareTo(subfield2number);
                }
                return field1number.compareTo(field2number);
            } catch (final Exception e) {
                // Игнорируем ошибку.
            }
            return 0;
        }
    }
}
