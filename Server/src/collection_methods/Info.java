package collection_methods;

import server_base.CollectionAdministrator;

public class Info extends SimpleMethod{

    public Info(CollectionAdministrator administrator) {
        super(administrator);
    }

    @Override
    public String run() {
        return getAdministrator().getInfo();
    }
}
