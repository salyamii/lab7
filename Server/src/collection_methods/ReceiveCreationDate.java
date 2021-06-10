package collection_methods;

import server_base.CollectionAdministrator;

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
