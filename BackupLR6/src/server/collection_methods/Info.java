package server.collection_methods;

import server.CollectionAdministrator;

public class Info extends SimpleMethod{

    public Info(CollectionAdministrator administrator) {
        super(administrator);
    }

    @Override
    public String run() {
        getAdministrator().save();
        return getAdministrator().getInfo();
    }
}
