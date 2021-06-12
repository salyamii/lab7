package collection_methods;

import server_base.CollectionAdministrator;

import java.sql.SQLException;

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
        getAdministrator().clear();
        getAdministrator().save();
        return "Collection is cleared.";
    }
}
