package clp.core.iso.core.requests;


import clp.core.iso.core.isorequests.ISOMessageContent;

public class ProcessingResponse
extends ISOMessageContent {
    public ProcessingResponse(final ISOMessageContent message) {
        this.setFields(message.getFields());
        this.setMTI(message.getMTI());
        this.setISOHeader(message.getISOHeader());
        this.setSourceMessage(message.getSourceMessage());
    }

    public ISOMessageContent getISOMessageContent() {
        return this;
    }

    public String getAuthorizationCode() {
        return this.getFields().get("38");
    }

    public int getResponseCode() {
        return Integer.parseInt(this.getFields().get("39"));
    }

    @Override
    public String toString() {
        return "Код ответа: " + System.lineSeparator() + getResponseCode() + System.lineSeparator()
        + super.toString();
    }
}
