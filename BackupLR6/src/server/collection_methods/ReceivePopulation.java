package server.collection_methods;

import server.CollectionAdministrator;

public class ReceivePopulation extends SimpleMethod{
    public ReceivePopulation(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receivePopulation();
        return null;
    }
}
