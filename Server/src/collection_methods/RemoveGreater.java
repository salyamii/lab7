package collection_methods;

import server_base.CollectionAdministrator;

public class RemoveGreater extends SimpleMethod{
    public RemoveGreater(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str) {
        getAdministrator().remove_greater(str);
        getAdministrator().save();
        return "Cities with greater population were removed.";
    }
}
