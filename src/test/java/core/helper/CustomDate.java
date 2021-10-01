package core.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDate extends Date {

    public CustomDate(long date) {
        super(date);
    }

    @Override
    public String toString(){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(this);
    }
}
