package collection_methods;

import server_base.CollectionAdministrator;

public class RemoveGreaterKey extends SimpleMethod{
    public RemoveGreaterKey(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str) {
        getAdministrator().remove_greater_key(str);
        getAdministrator().save();
        return "Elements with greater key were removed.";
    }
}
