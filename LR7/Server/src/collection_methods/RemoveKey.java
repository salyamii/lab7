package collection_methods;

import server_base.CollectionAdministrator;

public class RemoveKey extends SimpleMethod{
    public RemoveKey(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str){
        getAdministrator().remove_key(str);
        getAdministrator().save();
        return "City with key: "+ str + "was removed.";
    }
}
