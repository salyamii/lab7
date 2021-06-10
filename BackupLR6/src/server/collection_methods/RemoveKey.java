package server.collection_methods;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import server.CollectionAdministrator;

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
