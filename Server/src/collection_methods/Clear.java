package collection_methods;

import data.City;
import server_base.CollectionAdministrator;

import java.sql.SQLException;
import java.util.HashMap;

public class Clear extends SimpleMethod{
    public Clear(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String owner) {
        try {
            getAdministrator().getDatabaseHandler().clearCities(owner);
        } catch (SQLException sqlException) {
            return "Can not connect to database, so collection will not be cleared.";
        }
        HashMap<Long, City> newCities = getAdministrator().getDatabaseHandler().loadCollection();
        getAdministrator().setCities(newCities);
        return "Collection is cleared.";
    }
}
