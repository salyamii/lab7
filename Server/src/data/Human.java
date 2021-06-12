package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Shkurenko Nikita
 * @version 1.0
 * Class for describing field: Birthday of a governor
 */
@XmlRootElement(name = "governor")
@XmlAccessorType(XmlAccessType.NONE)
public class Human {
    public static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    @XmlElement
    private LocalDateTime birthday;

    @XmlElement
    private String birthdayString;

    /** Constructor for Human
     * @param birthday - birthday of a human
     */
    public Human(LocalDateTime birthday){
        this.birthday = birthday;
    }
    public Human(String birthdayString){ this.birthdayString = birthdayString;}

    public Human(){}

    public LocalDateTime getBirthday() {
        return birthday;
    }
    public String getBirthdayString(){ return birthday.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER)); }

    @Override
    public String toString() {
        return birthday.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER));
    }
}
