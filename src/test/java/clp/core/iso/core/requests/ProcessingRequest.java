package clp.core.iso.core.requests;




import clp.core.iso.core.isorequests.ISOMessageContent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ProcessingRequest
extends ISOMessageContent {
    public ProcessingRequest() {
        this(true);
    }

    public ProcessingRequest(boolean autoFill) {
        setISOHeader("A4M08000");
        if (autoFill) {
            Date date = new Date();
            setField("11", new StringBuilder(String.valueOf(date.getTime())).reverse().toString().substring(0, 5));
            setField("37", getFields().get("11"));
            setField("108", getFields().get("11"));
            SimpleDateFormat sdf_full = new SimpleDateFormat("MMddHHmmss");
            sdf_full.setTimeZone(TimeZone.getTimeZone("UTC"));
            setField("7", sdf_full.format(date));
            SimpleDateFormat sdf_time = new SimpleDateFormat("HHmmss");
            setField("12", sdf_time.format(date));
            setField("49", "643");
            SimpleDateFormat sdf_date = new SimpleDateFormat("MMdd");
            setField("13", sdf_date.format(date));
            setField("22", "901");
            setField("32", "00001");
            SimpleDateFormat merchant_date = new SimpleDateFormat("YYYYMMdd");
            setField("43.9", merchant_date.format(date));
            setField("43.11", "VB24");
            setField("18", "6011");
            setField("43.1", "DEVICE IN MOSCOW");
            setField("43.12", "DEVICE IN MOSCOW");
        }
    }

    public ISOMessageContent getISOMessageContent() {
        return this;
    }
}