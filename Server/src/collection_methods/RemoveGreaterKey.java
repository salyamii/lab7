package collection_methods;

import server_base.CollectionAdministrator;
import server_base.DatabaseHandler;

import java.sql.SQLException;

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
            return "Database ERROR. The collection will not be changed." ;
        }
        getAdministrator().remove_greater_key(str);
        //getAdministrator().save();
        return "Elements with greater key were removed.";
    }
}
