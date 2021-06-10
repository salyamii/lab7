package server.collection_methods;

import server.CollectionAdministrator;

import java.util.ArrayList;

public class Help extends SimpleMethod{
    public Help(CollectionAdministrator administrator){
        super(administrator);
    }
    @Override
    public String run() {
        CollectionAdministrator adm = getAdministrator();
        ArrayList<java.lang.String> arrayOfCommands = new ArrayList<>();
        arrayOfCommands = adm.getHelper();
        StringBuilder builder = new StringBuilder();
        for (String command : arrayOfCommands) {
            builder.append(command + '\n');
        }
        getAdministrator().save();
        return builder.toString();
    }
}
