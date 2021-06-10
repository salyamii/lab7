package server_base;

import collection_methods.*;
import data.*;
import server_base.CollectionAdministrator;
import server_base.ServerMain;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

import static server_base.CollectionAdministrator.dateTimeFormatter;

public class ServerUDP extends Thread{
    private final int TICKS_TO_DISCONNECT = 10;
    private int counter = 0;

    private DatagramSocket socket;

    private String jdbcURL = "jdbc:postgresql://localhost:4141/studs";
    private String usernameDB = "pg";
    private String passwordDB = "studs";
    CollectionAdministrator administrator = new CollectionAdministrator();
    DatabaseHandler databaseHandler = new DatabaseHandler(jdbcURL, usernameDB, passwordDB);
    Scanner in = new Scanner(System.in);
    HashMap<String, SimpleMethod> option = new HashMap<>();

    public ServerUDP(CollectionAdministrator administrator){
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
        option.put("execute_script", new ExecuteScript(administrator));
        option.put("remove_greater", new RemoveGreater(administrator));
        option.put("remove_greater_key", new RemoveGreaterKey(administrator));
        option.put("remove_lower_key", new RemoveLowerKey(administrator));
        option.put("group_counting_by_population", new GroupCountingByPopulation(administrator));
        option.put("count_by_establishment_date", new CountByEstablishmentDate(administrator));
        option.put("count_less_than_establishment_date", new CountLessThanEstablishmentDate(administrator));
        option.put("exit", new Exit(administrator));
    }

    /**
     * Method for starting a server app and realising connection with clients.
     */
    public void run (){
        System.out.println("Server is online.");
        boolean running = true;

        loadCollection(databaseHandler);

        try{
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException classNotFoundException){
            System.err.println("PostgreSQL driver has not found.");
            System.exit(-1);
        }

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
                        administrator.save();
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
            String[] received_arg = received.trim().split(" ", 2);
            String sent;
            if (received_arg[0].equals("exit")){
                sent = option.get(received_arg[0]).run();
                //System.out.println("Server is offline.");
            }
            else if(received_arg[0].equals("check")){
                sent = "Server is online.";
            }
            else if(received_arg[0].equals("check_id")){
                HashMap<Long, data.City> temp = administrator.getCities();
                try{
                    if(temp.containsKey(Long.parseLong(received_arg[1]))){
                        sent = "okay";
                    }
                    else
                        sent = "You are trying to update non-existing city.\n";
                }
                catch (NumberFormatException numberFormatException){
                    sent = "Invalid format of ID.\n";
                }

            }
            else
                sent = (received_arg.length == 1) ? option.get(received_arg[0]).run()
                    : option.get(received_arg[0]).run(received_arg[1]);
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

    /**
     * Method for loading data from database initially
     * @param databaseHandler
     * @return
     */
    public boolean loadCollection(DatabaseHandler databaseHandler){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            if (!databaseHandler.getConnection().isClosed()){
                databaseHandler.connectToDatabase();
                PreparedStatement joinStatement = databaseHandler.getConnection().prepareStatement("SELECT * FROM cities");
                ResultSet rs = joinStatement.executeQuery();
                while(rs.next()){
                    City newCity = new City((long) rs.getInt("id"),
                            rs.getString("name"),
                            new data.Coordinates(rs.getFloat("coordinates_x"), rs.getInt("coordinates_y")),
                            LocalDateTime.parse(rs.getString("creationDate"), formatter),
                            rs.getDouble("area"),
                            rs.getInt("population"),
                            rs.getFloat("metersAboveSeaLevel"),
                            LocalDate.parse(rs.getString("establishmentDate"), formatter),
                            rs.getInt("telephoneCode"),
                            Climate.valueOf(rs.getString("climate")),
                            new Human(LocalDateTime.parse(rs.getString("governor"), dateTimeFormatter)));
                    administrator.getCities().put(newCity.getId(), newCity);
                }
                return true;
            }
        } catch (SQLException sqlException) {
            System.out.println("Can not connect to database.");
            System.exit(-1);
        }
        return false;
    }
}
