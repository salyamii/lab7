package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveGovernor extends SimpleMethod{
    public ReceiveGovernor(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveGovernor();
        getAdministrator().save();
        return null;
    }
}
