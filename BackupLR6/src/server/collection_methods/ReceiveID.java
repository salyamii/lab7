package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveID extends SimpleMethod{
    public ReceiveID(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveID();
        getAdministrator().save();
        return null;
    }
}
