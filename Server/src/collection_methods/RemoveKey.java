package collection_methods;

import server_base.CollectionAdministrator;
import server_base.DatabaseHandler;

import java.sql.SQLException;

public class RemoveKey extends SimpleMethod{
    public RemoveKey(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str, DatabaseHandler databaseHandler, String owner){
        try{
            if (!databaseHandler.removeCityByID(Long.parseLong(str), owner))
                return "Access denied.";

        }
        catch (Exception sqlException){
            sqlException.printStackTrace();
            return "Something went wrong";
        }

        getAdministrator().remove_key(str);
        return "City with key: "+ str + " was removed.";
    }
}
