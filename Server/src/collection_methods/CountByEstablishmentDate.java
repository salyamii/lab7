package collection_methods;

import server_base.CollectionAdministrator;
import data.City;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class CountByEstablishmentDate extends SimpleMethod{
    public CountByEstablishmentDate(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str) {
        LocalDate establishmentDate;
        String date = str;
        try{
            establishmentDate = LocalDate.parse(date, CollectionAdministrator.formatter);
            int counter = 0;
            for(Map.Entry<Long, City> city : getAdministrator().getCities().entrySet()){
                if(city.getValue().getEstablishmentDate().isEqual(establishmentDate)){
                    counter++;
                }
            }
            return String.valueOf(counter) + " cities.";
        }
        catch (DateTimeParseException dateTimeParseException){
            return "Data must be xxxx-xx-xx format. Try again.";
        }
    }
}
