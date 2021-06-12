package collection_methods;

import data.City;
import server_base.CollectionAdministrator;
import server_base.DatabaseHandler;

import java.sql.SQLException;
import java.util.HashMap;

public class RemoveGreaterKey extends SimpleMethod{
    public RemoveGreaterKey(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str, DatabaseHandler databaseHandler, String owner) {
        try {
            databaseHandler.removeCitiesWithGreaterID(Long.parseLong(str), owner);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return "Database ERROR. Can not remove greater elements." ;
        }
        HashMap<Long, City> newCities = getAdministrator().getDatabaseHandler().loadCollection();
        getAdministrator().setCities(newCities);
        //getAdministrator().save();
        return "Elements with greater key were removed.";
    }
}
