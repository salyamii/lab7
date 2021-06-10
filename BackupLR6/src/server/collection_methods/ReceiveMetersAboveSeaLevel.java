package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveMetersAboveSeaLevel extends SimpleMethod{
    public ReceiveMetersAboveSeaLevel(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveMetersAboveSeaLevel();
        getAdministrator().save();
        return null;
    }
}
