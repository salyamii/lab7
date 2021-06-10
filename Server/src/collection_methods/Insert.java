package collection_methods;

import data.CityForParsing;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import server_base.CollectionAdministrator;
import data.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Insert extends SimpleMethod{

    public static final String DATE_FORMATTER = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    public Insert(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str) {
        try{
            XmlMapper xmlMapper = new XmlMapper();
            CityForParsing cityForParsing = xmlMapper.readValue(str, CityForParsing.class);
            City newCity = new City(getAdministrator().receiveID(), cityForParsing.getName(),
                    cityForParsing.getCoordinates(), getAdministrator().receiveCreationDate(),
                    cityForParsing.getArea(), cityForParsing.getPopulation(), cityForParsing.getMetersAboveSeaLevel(),
                    LocalDate.parse(cityForParsing.getEstablishmentDate(), formatter),
                    cityForParsing.getTelephoneCode(), cityForParsing.getClimate(),
                    new Human(LocalDateTime.parse(cityForParsing.getGovernor().getBirthday(), dateTimeFormatter)));
            getAdministrator().getCities().put(newCity.getId(), newCity);
            getAdministrator().save();
            return "A new city was inserted successfully.";
        }
        catch (Exception e){
            System.out.println("Incorrect deserializing.");
        }
        return "City wasn't inserted.";
    }
}
