package collection_methods;

import server_base.CollectionAdministrator;

import java.util.ArrayList;

public class Help extends SimpleMethod{
    public Help(CollectionAdministrator administrator){
        super(administrator);
    }
    @Override
    public String run() {
        CollectionAdministrator adm = getAdministrator();
        ArrayList<String> arrayOfCommands = new ArrayList<>();
        arrayOfCommands = adm.getHelper();
        StringBuilder builder = new StringBuilder();
        for (String command : arrayOfCommands) {
            builder.append(command + '\n');
        }
        return builder.toString();
    }
}
