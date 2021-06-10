package server.collection_methods;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import server.CollectionAdministrator;
import server.data.City;

import java.io.IOException;

public class UpdateID extends SimpleMethod{
    public UpdateID(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str){
        try{

            getAdministrator().save();
            return "City was updated.";
        }
        catch (Exception exception){
            return "Incorrect deserializing.";
        }

    }
}
