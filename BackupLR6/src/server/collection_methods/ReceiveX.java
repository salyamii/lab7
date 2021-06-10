package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveX extends SimpleMethod{
    public ReceiveX(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveX();
        return null;
    }
}
