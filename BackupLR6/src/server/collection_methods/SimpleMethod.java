package server.collection_methods;

import server.CollectionAdministrator;

import java.io.IOException;

public abstract class SimpleMethod {

    private CollectionAdministrator administrator;

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
}
