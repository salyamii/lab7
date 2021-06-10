package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveClimate extends SimpleMethod{
    public ReceiveClimate(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveClimate();
        getAdministrator().save();
        return null;
    }
}
