import data.City;

import javax.swing.*;
import java.util.*;

/** @author Shkurenko Nikita
 * @version 1.0
 * Method for handling user's command
 */
public class Commander {
    /** Collection manager for realising user's commands */
    private final CollectionManager collectionManager;
    /** Field for receiving user's commands */
    private String userCommand;
    /** Field for separating user inputs into a command and arg */
    private String []finalCommand;
    {
        userCommand = "";
    }
    /**
     * Constructor for making a commander
     * @param manager - CollectionManager class which object will realise user's commands
     */
    public Commander(CollectionManager manager){
        this.collectionManager = manager;
    }

    /**
     * Method for starting interactive mode
     */
    public void interactiveMode(){
        try{
            try(Scanner commandReader = new Scanner(System.in)){
                while(!userCommand.equals("exit")){
                    System.out.print("Enter a command: ");
                    userCommand = commandReader.nextLine();
                    finalCommand = userCommand.trim().toLowerCase().split(" ", 2);
                    try{
                        switch (finalCommand[0]){
                            case "":
                                break;
                            case "help":
                                collectionManager.help();
                                break;
                            case "info":
                                collectionManager.info();
                                break;
                            case "show":
                                collectionManager.show();
                                break;
                            case "insert":
                                collectionManager.insert();
                                break;
                            case "remove_key":
                                collectionManager.remove_key(finalCommand[1]);
                                break;
                            case "update_id":
                                collectionManager.update_id(finalCommand[1]);
                                break;
                            case "clear":
                                collectionManager.clear();
                                break;
                            case "save":
                                collectionManager.save();
                                break;
                            case "execute_script":
                                collectionManager.execute_script(finalCommand[1]);
                                break;
                            case "exit":
                                collectionManager.exit();
                                break;
                            case "remove_greater":
                                collectionManager.remove_greater(finalCommand[1]);
                                break;
                            case "remove_greater_key":
                                collectionManager.remove_greater_key(finalCommand[1]);
                                break;
                            case "remove_lower_key":
                                collectionManager.remove_lower_key(finalCommand[1]);
                                break;
                            case "group_counting_by_population":
                                collectionManager.group_counting_by_population();
                                break;
                            case "count_by_establishment_date":
                                collectionManager.count_by_establishment_date();
                                break;
                            case "count_less_than_establishment_date":
                                collectionManager.count_less_than_establishment_date();
                                break;
                            default:
                                System.out.println("Unknown command. Check help for information.");
                                break;
                        }
                    }catch(ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
                        System.out.println("Argument of command is absent. Check help for information.");
                    }

                }
            }
        }
        catch (NoSuchElementException noSuchElementException){
            System.out.println("Program will be finished now.");
            System.exit(1);
        }
    }

    /** Method for comparing elements */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Commander)) return false;
        Commander commander = (Commander) o;
        return Objects.equals(commander, commander.collectionManager);
    }
}
