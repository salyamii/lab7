package collection_methods;

import server_base.CollectionAdministrator;

public class Show extends SimpleMethod{

    public Show(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        return getAdministrator().getShow();
    }
}
