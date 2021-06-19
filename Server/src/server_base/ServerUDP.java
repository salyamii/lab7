package server_base;

import collection_methods.*;
import data.*;
import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;


public class ServerUDP extends Thread{
    private final int TICKS_TO_DISCONNECT = 10;
    private int counter = 0;

    // pools that are required
    ExecutorService readerPool = Executors.newFixedThreadPool(5);
    ExecutorService processingPool = Executors.newCachedThreadPool();
    ForkJoinPool sendingPool = ForkJoinPool.commonPool();
    ReentrantLock locker = new ReentrantLock();

    private DatagramSocket socket;
    private String owner;
    private CollectionAdministrator ca;

    Scanner in = new Scanner(System.in);
    HashMap<String, SimpleMethod> option = new HashMap<>();

    public ServerUDP(CollectionAdministrator administrator){
        ca = administrator;
        try{
            socket = new DatagramSocket(4149);
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
        //option.put("save", new Save(administrator));
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

        while(running){
            byte[] bufReceive = new byte[65535];
            DatagramPacket packet = new DatagramPacket(bufReceive, bufReceive.length);

            try{
                socket.receive(packet);
            }
            catch (IOException ioException){
                System.out.println("Invalid Object received.");
            }
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(bufReceive, bufReceive.length, address, port);
            DatagramPacket finalPacket = packet; // temp packet for reading thread.
            Future<String> readingResult = readerPool.submit(() ->
                    new String(finalPacket.getData(), 0, finalPacket.getLength()));

            String received = null;
            locker.lock();
            try {
                received = readingResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                System.out.println("Execution was interrupted.");
                continue;
            }
            finally {
                locker.unlock();
            }

            String[] received_arg = received.trim().split(" ", 3);
            String sent = "err";
            Future<String> processingResult;
            locker.lock();
            try{
                switch (received_arg[0]) {
                    case "exit":
                        processingResult = processingPool.submit(() -> option.get(received_arg[0]).run());
                        sent = processingResult.get();
                        break;
                    case "check":
                        processingResult = processingPool.submit(() -> "Server is online");
                        sent = processingResult.get();
                        break;
                    case "check_id":
                        processingResult = processingPool.submit(() -> checkIDAnswer(received_arg));
                        sent = processingResult.get();
                        break;
                    case "register":
                        processingResult = processingPool.submit(() -> registerAnswer(received_arg));
                        sent = processingResult.get();
                        break;
                    case "login":
                        processingResult = processingPool.submit(() -> loginAnswer(received_arg));
                        sent = processingResult.get();
                        break;
                    case "count_by_establishment_date":
                    case "count_less_than_establishment_date":
                        processingResult = processingPool.submit(() -> option.get(received_arg[0]).run(received_arg[1]));
                        sent = processingResult.get();
                        break;
                    default:
                        //System.out.println(Arrays.toString(received_arg));
                        if(received_arg.length == 1) processingResult = processingPool.submit(() ->
                                option.get(received_arg[0]).run());
                        else if(received_arg.length == 2) processingResult = processingPool.submit(() ->
                                option.get(received_arg[0]).run(received_arg[1]));
                            // 2 - object, 1 - owner
                        else processingResult = processingPool.submit(() ->
                                    option.get(received_arg[0]).run(received_arg[2],ca.getDatabaseHandler(), received_arg[1]));
                        sent = processingResult.get();
                        break;
                }
            }
            catch (InterruptedException | ExecutionException e){
                e.printStackTrace();
                System.out.println("Processing was failed.");
                sent = "Processing was failed.";
            }
            finally {
                locker.unlock();
            }

            String finalSent = sent;
            locker.lock();
            sendingPool.execute(() -> sendTask(finalSent, socket, address, port));
            locker.unlock();

        }
        socket.close();
    }

    public String checkIDAnswer(String[] received_arg){
        String sent = "err";
        try {
            if(ca.getDatabaseHandler().checkID(Long.parseLong(received_arg[1]))) sent = "okay";
            else sent = "You are trying to update non-existing city.";
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        catch (NumberFormatException numberFormatException) {
            sent = "Invalid format of ID.\n";
        }
        return sent;
    }

    public String registerAnswer(String[] received_arg){
        String sent = "err";
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
        return sent;
    }

    public String loginAnswer(String[] received_arg){
        String sent = "err";
        try{
            if(ca.getDatabaseHandler().validateUser(received_arg[1], received_arg[2]))
                sent = "You logged in successfully.";
            else
                sent = "Incorrect data.";
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
            sent = "Database is not available now. Try later.";
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
            sent = "Enter correct data, please.";
        }
        return sent;
    }

    public void sendTask(String sent, DatagramSocket socket, InetAddress address, int port){
        byte[] bufSend = sent.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(bufSend, bufSend.length, address, port);
        try{
            socket.send(sendPacket);
        }
        catch(IOException ioException){
            System.out.println("Invalid Object might sent.");
        }
    }
}
