package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import data.*;

import java.io.IOException;
import java.net.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ClientUDP {
    private DatagramSocket socket;
    private InetAddress address;
    XmlMapper mapper = new XmlMapper();
    private boolean running;
    private final Scanner in = new Scanner(System.in);
    HashSet<String> options = new HashSet<>();
    private byte[] buf = new byte[65535];
    private byte[] bufFromServer = new byte[65535];
    private String[] regOrLog = null;
    private String username = null;
    //Utility date formatters
    public static final String DATE_FORMATTER = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    public ClientUDP() {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(10000);
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException unknownHostException) {
            System.out.println("Can't find IP. Fix it.");
        } catch (SocketException socketException) {
            //C:\Users\happy\Desktop\ITMO\OldLR5\script.txt
            System.out.println("The socket could not be opened, or the socket could not bind to the specified local port.");
        }
        options.add("help");
        options.add("show");
        options.add("info");
        options.add("insert");
        options.add("update_id");
        options.add("remove_key");
        options.add("clear");
        options.add("execute_script");
        options.add("exit");
        options.add("remove_greater");
        options.add("remove_greater_key");
        options.add("remove_lower_key");
        options.add("group_counting_by_population");
        options.add("count_by_establishment_date");
        options.add("count_less_than_establishment_date");
    }
    private final ArrayList<String> helper = new ArrayList<>();
    {
        helper.add("help - display help for available commands.");
        helper.add("info - print all information about collection.");
        helper.add("show - print all elements in collection");
        helper.add("insert - add a new element");
        helper.add("update_id {id} - update an element with inserted id");
        helper.add("remove_key {id} - delete an element from collection by key");
        helper.add("clear - remove all elements from collection");
        //helper.add("save - saving the collection to the xml file");
        helper.add("execute_script {file_name} - executing script from the file");
        //helper.add("exit - exit the program (without saving)");
        helper.add("remove_greater {population} - removing elements with greater population field");
        helper.add("remove_greater_key {id} - remove elements with greater id (key)");
        helper.add("remove_lower_key {id} - remove elements with lower id (key)");
        helper.add("group_counting_by_population - group elements by population");
        helper.add("count_by_establishment_date {establishmentDate} - display amount of elements with inserted establishment date");
        helper.add("count_less_than_establishment_date {establishmentDate} - display amount of elements that have lower value of establishmentDate");

    }

    public void sendClient() {
        try{
            //registration at first
            boolean logFlag = true;
            System.out.println("You need to sign up/sign in. \n" +
                    "To sign up enter: register *login* *password*;\n" +
                    "To sign in enter: login *login* *password*.\n" +
                    "Amount of chars in username and password must be under 50.");
            while(logFlag){
                System.out.print("Enter your data: ");
                regOrLog = in.nextLine().split(" ", 3);
                if(regOrLog[1].length() > 50 || regOrLog[2].length() > 50){ continue; }

                switch (regOrLog[0]){
                    case "register":
                        buf = ("register " + regOrLog[1] + " " + regOrLog[2]).getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);

                        packet = new DatagramPacket(bufFromServer, bufFromServer.length);
                        socket.receive(packet);
                        String whatReceived = new String(packet.getData(), 0, packet.getLength());
                        if(whatReceived.equals("You registered a new user profile.")){
                            System.out.println(whatReceived);
                            logFlag = false;
                            username = regOrLog[1];
                        }
                        System.out.println(whatReceived);
                        break;
                    case "login":
                        buf = ("login " + regOrLog[1] + " " + regOrLog[2]).getBytes();
                        packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);

                        packet = new DatagramPacket(bufFromServer, bufFromServer.length);
                        socket.receive(packet);
                        whatReceived = new String(packet.getData(), 0, packet.getLength());
                        if(whatReceived.equals("You logged in successfully.")){
                            System.out.println(whatReceived);
                            logFlag = false;
                            username = regOrLog[1];
                        }
                        else System.out.println(whatReceived);
                        break;
                }
            }

            // then activating app
            running = true;
            while (running) {
                System.out.print("Enter an option: ");
                String option;
                String[] optionSplitted;
                while (true) {
                    option = in.nextLine();
                    optionSplitted = option.trim().split(" ", 2);
                    if (options.contains(optionSplitted[0]))
                        break;
                    System.out.print("Incorrect option! Try help for information.\nEnter an option: ");
                }
                try{
                    if(optionSplitted[0].equals("exit")){
                        running = false;
                        buf = ("exit").getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);


                        System.out.println("You exited an application.");
                        socket.close();
                        System.exit(0);
                    }
                   else if(optionSplitted[0].equals("insert")){
                        //checking if server is online
                        String check = "check";
                        buf = check.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                        packet = new DatagramPacket(bufFromServer, bufFromServer.length);
                        socket.receive(packet);
                        String whatReceived = new String(packet.getData(), 0, packet.getLength());
                        System.out.println(whatReceived);

                        String out = optionSplitted[0] + " " + username + " " + mapper.writeValueAsString(makeCity());
                        buf = out.getBytes();
                        packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                    }
                   else if(optionSplitted[0].equals("update_id")){
                       //checking if collection has element with certain id
                        String checkId = "check_id " + optionSplitted[1];
                        buf = checkId.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                        packet = new DatagramPacket(bufFromServer, bufFromServer.length);
                        socket.receive(packet);

                        String whatReceived = new String(packet.getData(), 0, packet.getLength());
                        if(whatReceived.equals("okay")){
                            String out = optionSplitted[0] + " " + username +  " " +
                                    mapper.writeValueAsString(updateCity(Long.parseLong(optionSplitted[1])));
                            buf = out.getBytes();
                             packet = new DatagramPacket(buf, buf.length, address, 4141);
                            socket.send(packet);
                        }
                        else{
                            System.out.println(whatReceived);
                            continue;
                        }
                    }
                    else if(optionSplitted[0].equals("remove_key")){
                        String out = optionSplitted[0] + " " + username + " " + optionSplitted[1];
                        buf = out.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                    }
                    else if(optionSplitted[0].equals("remove_greater_key")){
                        String out = optionSplitted[0] + " " + username + " " + optionSplitted[1];
                        buf = out.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                    }
                    else if(optionSplitted[0].equals("remove_lower_key")){
                        String out = optionSplitted[0] + " " + username + " " + optionSplitted[1];
                        buf = out.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                    }
                    else if(optionSplitted[0].equals("remove_greater")){
                        String out = optionSplitted[0] + " " + username + " " + optionSplitted[1];
                        buf = out.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                    }
                    else if(optionSplitted[0].equals("help")){
                        for (String s : helper) {
                            System.out.println(s);
                        }
                        continue;
                    }
                    else if(optionSplitted[0].equals("info")){
                        String out = "info";
                        buf = out.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                    }
                    else if(optionSplitted[0].equals("show")){
                        String out = "show";
                        buf = out.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                    }
                    else if(optionSplitted[0].equals("clear")){
                        String out = "clear " + username;
                        buf = out.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                    }
                    else if(optionSplitted[0].equals("group_counting_by_population")){
                        String out = "group_counting_by_population";
                        buf = out.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);
                    }
                    else{

                        String out = optionSplitted.length == 1 ? optionSplitted[0] : optionSplitted[0] + " " + optionSplitted[1];
                        buf = out.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4141);
                        socket.send(packet);

                    }
                }
                catch (JsonProcessingException jsonProcessingException){
                    System.out.println("Processing exception when sending..");
                    System.exit(1);
                }
                catch (SocketTimeoutException socketTimeoutException){
                    System.out.println("Server is offline.");
                    continue;
                }
                catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
                    System.out.println("You forgot to insert ID of a city.");
                    continue;
                }
                DatagramPacket packet = new DatagramPacket(bufFromServer, bufFromServer.length);
                //System.out.println("Receiving packet...");
                try{
                    socket.receive(packet);
                    String whatReceived = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(whatReceived);
                }
                catch(SocketTimeoutException socketTimeoutException){
                    System.out.println("Server is not responding.");
                }

            }
        }
       catch (IOException ioException){
           System.err.println("\nThe server is not available now. Try later.");
           System.exit(1);
       }
        catch(NoSuchElementException noSuchElementException){
            System.out.println("\nInput faced loop, skipped.");
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
            System.out.println("Some data is absent. Try again.");
            sendClient();
        }
    }

    public void close(){
        socket.close();
    }


    public CityForParsing updateCity(long id){
        for( ; ; ){
            try{
                return new CityForParsing(id, receiveName(), receiveCoordinates(),
                        receiveArea(), receivePopulation(), receiveMetersAboveSeaLevel(),
                        receiveEstablishmentDateString(), receiveTelephoneCode(),
                        receiveClimate(), receiveGovernorString());
            }
            catch(InputMismatchException inputMismatchException){ System.out.println("Enter a long-type value, please.");
            }
        }
    }

    public CityForParsing makeCity(){
        return new CityForParsing(0, receiveName(), receiveCoordinates(),
                receiveArea(), receivePopulation(), receiveMetersAboveSeaLevel(),
                receiveEstablishmentDateString(), receiveTelephoneCode(),
                receiveClimate(), receiveGovernorString());
    }



    //Receiving methods for making an City object
    /** Method for receiving name
     *
     * @return String name
     */
    public String receiveName(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter a name of a town.");
                String name = in.nextLine().trim();
                if(name.equals("")){
                    System.out.println("This value can't be empty.");
                    continue;
                }
                return name;
            }
            catch (InputMismatchException inputMismatchException){
                System.out.println("This value must be String.");
            }
        }
    }

    /**
     * Method for receiving X coordinate
     * @return float X
     */
    public float receiveX(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter X coordinate in a float type. Value must be greater than -944 and can't be empty.");
                float x;
                try{
                    x = in.nextFloat();
                }
                catch(NoSuchElementException noSuchElementException){
                    x = 0;
                }
                String strX = Float.toString(x);
                if(x < -944){
                    System.out.println("Value can't be lower than -944.");
                    continue;
                }
                if(strX.equals("")){
                    System.out.println("Value can't be empty.");
                    continue;
                }
                return x;
            }
            catch (InputMismatchException inputMismatchException){
                System.out.println("This value must be a float-number type. Try again.");
            }
        }
    }

    /**
     * Method for receiving y coordinate
     * @return int Y
     */
    public int receiveY(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.print("Enter Y coordinate in a int type. Value can't be empty.");
                int y;
                try{
                    y = in.nextInt();
                }
                catch(NoSuchElementException noSuchElementException){
                    y = 0;
                }
                String strY = Integer.toString(y);
                if(strY.equals("")){
                    System.out.println("Value can't be empty.");
                    continue;
                }
                return y;
            }
            catch (InputMismatchException inputMismatchException){
                System.out.println("This value must be a int-number type. Try again.");
            }
        }
    }
    /**
     * Method for receiving Coordinate-type field
     * @return Coordinates coordinates
     */
    public Coordinates receiveCoordinates(){
        return new Coordinates(receiveX(), receiveY());
    }
    /**
     * Method for receiving an area
     * @return double area
     */
    public double receiveArea(){
        for( ; ; ) {
            try {
                System.out.println("Enter a number of area in double format. Value must be more that 0 and can't be empty.");
                Scanner in = new Scanner(System.in);
                double num;
                try{
                    num = in.nextDouble();
                }
                catch(NoSuchElementException noSuchElementException){
                    num = 0;
                }
                String strNum = Double.toString(num);
                if (num <= 0) {
                    System.out.println("Value must be more that 0.");
                    continue;
                }
                if(strNum.equals("")){
                    System.out.println("Value can't be empty.");
                    continue;
                }
                return num;
            }
            catch(InputMismatchException inputMismatchException){
                System.out.println("The value must be double type. Try again.");
            }
        }
    }

    /**
     * Method for receiving population
     * @return int population
     */
    public int receivePopulation(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter number of population. Value must be greater than 0 and can't be empty.");
                int pop;
                try{
                    pop = in.nextInt();
                }
                catch(NoSuchElementException noSuchElementException){
                    pop = 1;
                }
                String strPop = Integer.toString(pop);
                if(pop < 0){
                    System.out.println("Value must be greater than zero.");
                    continue;
                }
                if(strPop.equals("")){
                    System.out.println("Value can't be empty.");
                    continue;
                }
                return pop;
            }
            catch(InputMismatchException inputMismatchException){
                System.out.println("Value must be int-type format.");
            }
        }
    }

    /**
     * Method for receiving meters above sea level
     * @return float metersAboveSeaLevel
     */
    public float receiveMetersAboveSeaLevel(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter meters above sea level. Value can't be empty.");
                float meters;
                try{
                    meters = in.nextFloat();
                }
                catch (NoSuchElementException noSuchElementException){
                    meters = 0;
                }
                String strMeters = Float.toString(meters);
                if(strMeters.equals("")){
                    System.out.println("Value can't be empty.");
                    continue;
                }
                return meters;
            }
            catch(InputMismatchException inputMismatchException){
                System.out.println("Value must be float-type. Try again.");
            }
        }
    }

    /**
     * Method for receiving establishment date
     * @return LocalDate establishmentDate
     */
    public LocalDate receiveEstablishmentDate() {
        for ( ; ; ) {
            try {
                Scanner in = new Scanner(System.in);
                System.out.println("Enter an establishment date in format yyyy-MM-dd.");
                String date = in.nextLine();
                if (date.equals("")) {
                    System.out.println("Date value can't be empty.");
                    continue;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
                LocalDate establishmentDate = LocalDate.parse(date, formatter);
                return establishmentDate;
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("Date is a String object. Try again.");
            } catch (IllegalArgumentException | DateTimeParseException dateTimeParseException) {
                System.out.println("Invalid date format. Try again.");
            }
        }
    }

    /**
     * Method for receiving telephone code
     * @return int telephoneCode
     */
    public int receiveTelephoneCode(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter a telephone code. Value must be greater than 0, but lower than 100000.");
                int code;
                try{
                    code = in.nextInt();
                }
                catch(NoSuchElementException noSuchElementException){
                    System.out.println("Value of code must be int-type. Try again.");
                    continue;
                }
                String strCode = Integer.toString(code);
                if(strCode.equals("")){
                    System.out.println("Code can't be empty.");
                    continue;
                }
                if(code <= 0){
                    System.out.println("Code must be greater than 0.");
                    continue;
                }
                if(code > 100000){
                    System.out.println("Code must be lower than 100000.");
                    continue;
                }
                return code;
            }catch (InputMismatchException inputMismatchException){
                System.out.println("Value of code must be int-type. Try again.");
            }
        }
    }

    /**
     * Method for receiving a climate
     * @return Climate
     */
    public Climate receiveClimate(){
        for( ; ; ){
            try {
                Scanner in = new Scanner(System.in);
                System.out.println("You need to choose one of the options.");
                System.out.println("1 - Humid continental");
                System.out.println("2 - Monsoon");
                System.out.println("3 - Subarctic");
                System.out.println("4 - Tundra");
                System.out.print("Choose 1, 2, 3 or 4: ");
                int number;
                try{
                    number = in.nextInt();
                }
                catch(NoSuchElementException noSuchElementException){
                    number = 1;
                }
                switch (number){
                    case 1:
                        return Climate.HUMIDCONTINENTAL;
                    case 2:
                        return Climate.MONSOON;
                    case 3:
                        return Climate.SUBARCTIC;
                    case 4:
                        return Climate.TUNDRA;
                    default:
                        break;
                }
                System.out.println("You should enter 1, 2, 3 or 4. Try again.");
            }
            catch(InputMismatchException inputMismatchException){
                System.out.println("This value must be a number (1, 2, 3, 4). Choose one and try again.");
            }
        }
    }
    /** Method for receiving a governor
     *
     * @return Human governor
     */
    public Human receiveGovernor(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter a date and time of birth with a format: yyyy-MM-dd hh:MM:ss.");
                String dateTime = in.nextLine();
                LocalDateTime birthday = LocalDateTime.parse(dateTime, dateTimeFormatter);
                return new Human(birthday);
            }
            catch(InputMismatchException inputMismatchException){
                System.out.println("Input value must be String format.");
            }
            catch (DateTimeException dateTimeException){
                System.out.println("Your data format is invalid. Try again.");
            }
        }
    }
    public String receiveEstablishmentDateString() {
        for ( ; ; ) {
            try {
                Scanner in = new Scanner(System.in);
                System.out.println("Enter an establishment date in format yyyy-MM-dd.");
                String date;
                try{
                    date = in.nextLine();
                }
                catch(NoSuchElementException noSuchElementException){
                    date = "1997-01-01";
                }
                if (date.equals("")) {
                    System.out.println("Date value can't be empty.");
                    continue;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
                LocalDate establishmentDate = LocalDate.parse(date, formatter);
                return date;
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("Date is a String object. Try again.");
            } catch (IllegalArgumentException | DateTimeParseException exception) {
                System.out.println("Invalid date format. Try again.");
            }
        }
    }
    public HumanForParsing receiveGovernorString(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter a date and time of birth with a format: yyyy-MM-dd hh:MM:ss.");
                String dateTime;
                try{
                    dateTime = in.nextLine();
                }
                catch(NoSuchElementException noSuchElementException){
                    dateTime = "1997-01-01 00:00:00";
                }
                LocalDateTime birthday = LocalDateTime.parse(dateTime, dateTimeFormatter);
                return new HumanForParsing(dateTime);
            }
            catch(InputMismatchException inputMismatchException){
                System.out.println("Input value must be String format.");
            }
            catch (DateTimeException dateTimeException){
                System.out.println("Your data format is invalid. Try again.");
            }
        }
    }

}
