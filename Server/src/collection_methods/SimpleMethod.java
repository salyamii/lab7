package collection_methods;

import server_base.CollectionAdministrator;
import server_base.DatabaseHandler;
import server_base.ServerUDP;

public abstract class SimpleMethod {

    CollectionAdministrator administrator;

    public SimpleMethod(CollectionAdministrator administrator){
        this.administrator = administrator;
    }

    public void setAdministrator(CollectionAdministrator administrator) { this.administrator = administrator; }

    public CollectionAdministrator getAdministrator(){
        return administrator;
    }

    public String run(){
        return "Simple method with no arg was invoked.";
    }

    public String run(String str) {
        return "Simple method with arg was invoked.";
    }

    public String run(String str, DatabaseHandler databaseHandler) {return "arg + database handler";}

    public String run(String str, DatabaseHandler databaseHandler, String owner) {return "arg + database handler + owner";}

}
