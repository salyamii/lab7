package collection_methods;

import server_base.CollectionAdministrator;
import server_base.DatabaseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ExecuteScript extends SimpleMethod{
    public ExecuteScript(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run(String path, DatabaseHandler databaseHandler, String owner) {
        getAdministrator().getIfRecursive().add(path);
        File file = new File(path);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            return "Can not find your file.";
        }
        String []finalCommand;
        String command;
        while(sc.hasNextLine()) {
            command = sc.nextLine();
            finalCommand = command.trim().toLowerCase().split(" ", 3);
            try {
                switch (finalCommand[0]) {
                    case "help":
                        new Help(getAdministrator()).run();
                        break;
                    case "info":
                        new Info(getAdministrator()).run();
                        break;
                    case "show":
                        new Show(getAdministrator()).run();
                        break;
//                    case "insert":
//                        new Insert(getAdministrator()).run();
//                        break;
//                    case "update_id":
//                        new UpdateID(getAdministrator());
//                        break;
                    case "remove_key"://1 - username 2 - id
                        new RemoveKey(getAdministrator()).run(finalCommand[2],
                                getAdministrator().getDatabaseHandler(), finalCommand[2]);
                        break;
                    case "clear"://1 - username
                        new Clear(getAdministrator()).run(finalCommand[1]);
                        break;
                    case "execute_script":
                        if (!getAdministrator().getIfRecursive().contains(finalCommand[1])) {
                            getAdministrator().getIfRecursive().add(finalCommand[1]);
                            new ExecuteScript(getAdministrator()).run(finalCommand[1],
                                    getAdministrator().getDatabaseHandler());
                        } else {
                            System.out.println("Faced recursive script.");
                        }
                        break;
                    case "exit":
                        new Exit(getAdministrator()).run();
                    case "remove_greater":
                        new RemoveGreater(getAdministrator()).run(finalCommand[2],
                                getAdministrator().getDatabaseHandler(), finalCommand[1]);
                        break;
                    case "remove_greater_key":
                        new RemoveGreaterKey(getAdministrator()).run(finalCommand[2],
                                getAdministrator().getDatabaseHandler(), finalCommand[1]);
                        break;
                    case "remove_lower_key":
                        new RemoveLowerKey(getAdministrator()).run(finalCommand[2],
                                getAdministrator().getDatabaseHandler(), finalCommand[1]);
                        break;
                    case "group_counting_by_population":
                        new GroupCountingByPopulation(getAdministrator()).run();
                        break;
                    case "count_by_establishment_date":
                        new CountByEstablishmentDate(getAdministrator()).run(finalCommand[1]);
                        break;
                    case "count_less_than_establishment_date":
                        new CountLessThanEstablishmentDate(getAdministrator()).run(finalCommand[1]);
                    default:
                        return "Unknown command in script file.";
                }
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                return "Some arguments for command in script file are absent.";
            }
        }
        return "Script: " + path + " was executed.";
    }
}


