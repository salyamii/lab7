package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveY extends SimpleMethod{
    public ReceiveY(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveY();
        return null;
    }
}
