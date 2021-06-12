package collection_methods;

import data.City;
import server_base.CollectionAdministrator;
import server_base.DatabaseHandler;

import java.sql.SQLException;
import java.util.HashMap;

public class RemoveGreater extends SimpleMethod{
    public RemoveGreater(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str, DatabaseHandler databaseHandler, String owner) {
        try{
            databaseHandler.removeCitiesByGreaterPopulation(Integer.parseInt(str), owner);
        }
        catch (SQLException sqlException){
            return "Database ERROR. Can not remove elements with greater population value.";
        }
        HashMap<Long, City> newCities = getAdministrator().getDatabaseHandler().loadCollection();
        getAdministrator().setCities(newCities);
        return "Cities with greater population were removed.";
    }
}
