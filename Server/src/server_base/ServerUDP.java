package server_base;

import collection_methods.*;
import data.*;
import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class ServerUDP extends Thread{
    private final int TICKS_TO_DISCONNECT = 10;
    private int counter = 0;

    private DatagramSocket socket;
    private String owner;
    private CollectionAdministrator ca;

    Scanner in = new Scanner(System.in);
    HashMap<String, SimpleMethod> option = new HashMap<>();

    public ServerUDP(CollectionAdministrator administrator){
        ca = administrator;
        try{
            socket = new DatagramSocket(4141);
            socket.setSoTimeout(10000);
        }
        catch (SocketException socketException){
            System.out.println("Something wrong with socket you chosen.");
        }
        option.put("help", new Help(administrator));
        option.put("info", new Info(administrator));
        option.put("show", new Show(administrator));
        option.put("insert", new Insert(administrator));
        option.put("update_id", new UpdateID(administrator));
        option.put("remove_key", new RemoveKey(administrator));
        option.put("clear", new Clear(administrator));
        option.put("save", new Save(administrator));
        option.put("execute_script", new ExecuteScript(administrator));
        option.put("remove_greater", new RemoveGreater(administrator));
        option.put("remove_greater_key", new RemoveGreaterKey(administrator));
        option.put("remove_lower_key", new RemoveLowerKey(administrator));
        option.put("group_counting_by_population", new GroupCountingByPopulation(administrator));
        option.put("count_by_establishment_date", new CountByEstablishmentDate(administrator));
        option.put("count_less_than_establishment_date", new CountLessThanEstablishmentDate(administrator));
        option.put("exit", new Exit(administrator));
    }
    public CollectionAdministrator getCA(){
        return ca;
    }
    private DatabaseHandler databaseHandler;
    /**
     * Method for starting a server app and realising connection with clients.
     */
    public void run (){
        System.out.println("Server is online.");
        boolean running = true;

        while(running){
            byte[] bufReceive = new byte[65535];
            DatagramPacket packet = new DatagramPacket(bufReceive, bufReceive.length);
            try{
                socket.receive(packet);
            }
            catch(SocketTimeoutException socketTimeoutException){
                System.out.println("Client does not respond...");
                counter++;
                if(counter == TICKS_TO_DISCONNECT){
                    System.out.print("Disconnect?" +
                            "\nY - to disconnect" +
                            "\nAny key - to stay online" +
                            "\nsave - to save collection forcefully: ");
                    String exit = in.nextLine();
                    exit = exit.trim();
                    if(exit.equals("Y")) {
                        socket.close();
                        System.out.println("Server is offline.");
                        System.exit(0);
                    }else if(exit.equals("save")){
                        option.get("save").run();
                        System.out.println("Collection was saved forcefully.");
                    }
                    counter = 0;
                }
                continue;
            }catch (IOException ioException){
                System.out.println("Invalid Object received.");
            }
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(bufReceive, bufReceive.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());
            String[] received_arg = received.trim().split(" ", 3);
            String sent = "xxx";
            switch (received_arg[0]) {
                case "exit":
                    sent = option.get(received_arg[0]).run();
                    //System.out.println("Server is offline.");
                    break;
                case "check":
                    sent = "Server is online.";
                    break;
                case "check_id":
                    try {
                        if(ca.getDatabaseHandler().checkID(Long.parseLong(received_arg[1]))) sent = "okay";
                        else sent = "You are trying to update non-existing city.";
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                    catch (NumberFormatException numberFormatException) {
                        sent = "Invalid format of ID.\n";
                    }

                    break;
                case "register":
                    try {
                        if(ca.getDatabaseHandler().registerUser(received_arg[1], received_arg[2])){
                            sent = "You registered a new user.";
                        }
                        else
                            sent = "This username is unavailable. Try another one.";
                    } catch (SQLException sqlException) {
                        sent = "Database is unavailable now. Try later.";
                        sqlException.printStackTrace();
                    }
                    catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
                        sent = "Enter correct data, please.";
                    }
                    break;
                case "login":
                    try{
                        if(ca.getDatabaseHandler().validateUser(received_arg[1], received_arg[2]))
                            sent = "You logged in successfully.";
                        else
                            sent = "Incorrect data.";
                    }
                    catch (SQLException sqlException){
                        sent = "Database is not available now. Try later.";
                    }
                    catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
                        sent = "Enter correct data, please.";
                    }
                    break;
                default:
                    //System.out.println(Arrays.toString(received_arg));
                    if(received_arg.length == 1) sent = option.get(received_arg[0]).run();
                    else if(received_arg.length == 2) sent = option.get(received_arg[0]).run(received_arg[1]);
                    // 2 - object, 1 - owner
                    else sent = option.get(received_arg[0]).run(received_arg[2],ca.getDatabaseHandler(), received_arg[1]);
                    /*sent = (received_arg.length == 1) ? option.get(received_arg[0]).run()
                            : option.get(received_arg[0]).run(received_arg[1]);*/
                    break;
            }
            byte[] bufSend = sent.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(bufSend, bufSend.length, address, port);
            try{
                socket.send(sendPacket);
            }
            catch(IOException ioException){
                System.out.println("Invalid Object sent.");
            }

        }
        socket.close();
    }
}
