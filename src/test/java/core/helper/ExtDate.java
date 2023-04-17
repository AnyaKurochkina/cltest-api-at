package core.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtDate extends Date {
    String microseconds;

    public ExtDate(String date) throws ParseException {
        super(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(date.substring(0, 20) + Integer.parseInt(date.substring(20, 26))/1000).getTime());
        microseconds = date.substring(20, 26);
    }

    @Override
    public String toString(){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.").format(this) + microseconds + "Z";
    }
}
