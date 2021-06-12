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
            databaseHandler.removeCityByID(Long.parseLong(str), owner);
        }
        catch (Exception sqlException){
            sqlException.printStackTrace();
            return "Access denied.";
        }

        getAdministrator().remove_key(str);
        //getAdministrator().save();
        return "City with key: "+ str + " was removed.";
    }
}
