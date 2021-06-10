package collection_methods;

import server_base.CollectionAdministrator;

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
