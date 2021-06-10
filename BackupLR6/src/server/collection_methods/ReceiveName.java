package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveName extends SimpleMethod{
    public ReceiveName(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveName();
        getAdministrator().save();
        return null;
    }
}
