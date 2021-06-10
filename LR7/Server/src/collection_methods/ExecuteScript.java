package collection_methods;

import server_base.CollectionAdministrator;

public class ExecuteScript extends SimpleMethod{
    public ExecuteScript(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String str) {
        getAdministrator().execute_script(str);
        getAdministrator().save();
        return "Script: " + str + " was executed.";
    }
}
