package server.collection_methods;

import server.CollectionAdministrator;

public class ReceiveEstablishmentDate extends SimpleMethod{
    public ReceiveEstablishmentDate(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveEstablishmentDate();
        getAdministrator().save();
        return null;
    }
}
