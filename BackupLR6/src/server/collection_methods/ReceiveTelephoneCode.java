package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveTelephoneCode extends SimpleMethod{
    public ReceiveTelephoneCode(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveTelephoneCode();
        getAdministrator().save();
        return null;
    }
}
