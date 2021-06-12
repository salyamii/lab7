package collection_methods;

import data.City;
import server_base.CollectionAdministrator;
import server_base.DatabaseHandler;

import java.sql.SQLException;
import java.util.HashMap;

public class RemoveLowerKey extends SimpleMethod{
    public RemoveLowerKey(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str, DatabaseHandler databaseHandler, String owner) {
        try{
            databaseHandler.removeCitiesWithLowerID(Long.parseLong(str), owner);
        }
        catch (SQLException sqlException){
            return "Database ERROR. Can not remove lower elements.";
        }
        HashMap<Long, City> newCities = getAdministrator().getDatabaseHandler().loadCollection();
        getAdministrator().setCities(newCities);
        return "Elements with lower keys were removed.";
    }
}
