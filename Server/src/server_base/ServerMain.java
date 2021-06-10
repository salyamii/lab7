package server_base;

import server_base.CollectionAdministrator;
import utility_methods.IsFileValid;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerMain {
    private static String path;
    public static void main(String[] args) {
        System.out.println("Server starts running...");
        try{
            //C:\Users\happy\Desktop\xd\test.xml
            ServerUDP serverUDP = new ServerUDP(new CollectionAdministrator(args[0]));
            serverUDP.run();
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
            for( ; ; ){
                System.out.print("Enter a correct path to the XML file: ");
                Scanner in = new Scanner(System.in);
                try{
                    path = in.nextLine();
                    if(!IsFileValid.run(path)){
                        continue;
                    }
                    ServerUDP serverUDP = new ServerUDP(new CollectionAdministrator(path));
                    serverUDP.run();
                }
                catch (NoSuchElementException noSuchElementException){
                    System.out.println("No files were inserted.");
                }

            }

        }
    }

    public static String getPath() {
        return path;
    }
}
