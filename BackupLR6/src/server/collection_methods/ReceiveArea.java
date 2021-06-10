package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveArea extends SimpleMethod{
    public ReceiveArea(CollectionAdministrator administrator) {
        super(administrator);
    }


    @Override
    public String run() {
        getAdministrator().receiveArea();
        getAdministrator().save();
        return null;
    }
}
