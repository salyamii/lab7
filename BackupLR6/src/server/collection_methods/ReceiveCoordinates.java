package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveCoordinates extends SimpleMethod{
    public ReceiveCoordinates(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveCoordinates();
        getAdministrator().save();
        return null;
    }
}
