package server.collection_methods;

import server.CollectionAdministrator;

public class Show extends SimpleMethod{

    public Show(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        return getAdministrator().getInfo();
    }
}
