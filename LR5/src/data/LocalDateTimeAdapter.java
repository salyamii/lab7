package data;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public String marshal(LocalDateTime dateTime){
        return dateTime.format(dateFormat);
    }

    public LocalDateTime unmarshal (String dateTime){
        return LocalDateTime.parse(dateTime, dateFormat);
    }
}
