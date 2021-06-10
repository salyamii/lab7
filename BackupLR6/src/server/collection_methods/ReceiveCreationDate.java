package server.collection_methods;

import server.CollectionAdministrator;

import java.text.CollationElementIterator;

public class ReceiveCreationDate extends SimpleMethod{
    public ReceiveCreationDate(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().receiveCreationDate();
        getAdministrator().save();
        return null;
    }
}
