import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import data.*;

/**
 * @author Shkurenko Nikita
 * @version 1.0
 * Class for realising commands
 */

public class CollectionManager {
    /** HashSet collection to keep a collection as a java object */
    private final HashMap<Long, City> cities;
    /** Field used for saving collection into xml file */
    private File xmlCollection;
    /** Field for saving date of initialization thw collection */
    private ZonedDateTime initializationDate;
    /** Field for checking the program was started */
    private boolean wasStart;
    public static final String DATE_FORMATTER = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMATTER = "yyyy-MM-dd hh:MM:ss";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
    /** HashMap collection for making a manual */
    private final HashMap<String, String> commandsInfo;
    {
        wasStart = false;
        cities = new HashMap<Long, City>();

        /** Manual of commands */
        commandsInfo = new HashMap<>();
        commandsInfo.put("help", " - display help for available commands");
        commandsInfo.put("info", " - print all information about collection");
        commandsInfo.put("show"," - print all elements in string representation to standard output");
        commandsInfo.put("insert_null {element}", " - add a new element with a key");
        commandsInfo.put("update_id {element}", " - refresh an element's value in collection (using id)");
        commandsInfo.put("remove_key null", " - delete an element from collection by key");
        commandsInfo.put("clear", " - clear the collection");
        commandsInfo.put("save", " - save the collection to the file");
        commandsInfo.put("execute_script file_name", " - read and execute script from the file");
        commandsInfo.put("exit", " - exit the program (without saving)");
        commandsInfo.put("remove_greater {element}", " - delete all elements that are greater");
        commandsInfo.put("remove_greater_key null", " - delete all elements that have a key greater than inserted");
        commandsInfo.put("remove_lower_key null", " - delete all elements that have a key lower than inserted");
        commandsInfo.put("group_counting_by_population", " - group elements by population");
        commandsInfo.put("count_by_establishment_date establishmentDate", " - display amount of elements with inserted establishment date");
        commandsInfo.put("count_less_than_establishment_date establishmentDate", " - display amount of elements that have lower value of establishmentDate");

    }

    // Constructor for checking a path to file existence and file readiness to work
    public CollectionManager(){
        Scanner in = new Scanner(System.in);
        try{
            for( ; ; ){
                System.out.print("Enter a full path to XML file with collection: ");
                String pathToFile = in.nextLine();
                if(checkFile(pathToFile)){
                    try{
                        final QName qName = new QName("cities");
                        InputStream inputStream = new FileInputStream(new File(pathToFile));

                        // create xml event reader for input stream
                        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
                        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader((inputStream));

                        // initialize jaxb
                        JAXBContext context = JAXBContext.newInstance(City.class);
                        Unmarshaller unmarshaller = context.createUnmarshaller();
                        XMLEvent e;

                        //Field for counting amount of downloaded elements
                        int counterGood = 0;
                        int counterBad = 0;

                        //Loop for unmarshalling collection
                        while((e = xmlEventReader.peek()) != null){
                            //check the event is a Document start element
                            if(e.isStartDocument() && ((StartElement) e).getName().equals(qName)){
                                //unmarshalling the document
                                City unmarshalledCity = unmarshaller.unmarshal(xmlEventReader, City.class).getValue();
                                Coordinates unmarshalledCoordinates = unmarshaller.unmarshal(xmlEventReader,Coordinates.class).getValue();
                                Human unmarshalledHuman = unmarshaller.unmarshal(xmlEventReader, Human.class).getValue();
                                if(unmarshalledCity.getName() != null && !unmarshalledCity.getName().equals("")
                                        && unmarshalledCity.getCoordinates() != null && unmarshalledCity.getCreationDate() != null
                                        && unmarshalledCity.getArea() > 0 && unmarshalledCity.getPopulation() > 0
                                        && unmarshalledCity.getTelephoneCode() > 0 && unmarshalledCity.getTelephoneCode() <= 1000000
                                        && unmarshalledCity.getClimate() != null && unmarshalledCity.getGovernor() != null
                                        && unmarshalledCoordinates.getX() > -944){
                                    long idCity;
                                    Random random = new Random();
                                    while(true){
                                        idCity = random.nextInt(1000);
                                        if(!cities.containsKey(idCity)){
                                            break;
                                        }
                                    }
                                    unmarshalledCity.setId(idCity);
                                    unmarshalledCity.setCreationDate(receiveCreationDate());
                                    cities.put(idCity, unmarshalledCity);
                                    counterGood++;
                                }
                                else{
                                    counterBad++;
                                }
                            } else{
                                xmlEventReader.next();
                                xmlEventReader.next();
                            }
                        }
                        System.out.println("Collection was loaded successfully. " + counterGood + " elements have been loaded.");
                        System.out.println("Amount of elements which contains invalid values and can't be loaded: " + counterBad);
                        xmlCollection = new File(pathToFile);
                        wasStart = true;
                        initializationDate = ZonedDateTime.now();
                        break;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (XMLStreamException e) {
                        e.printStackTrace();
                    } catch (JAXBException e) {
                        e.printStackTrace();
                    }
                }else {
                    System.out.println("Try again.");
                }
            }
        } catch (FactoryConfigurationError factoryConfigurationError) {
            factoryConfigurationError.printStackTrace();
        }
        catch(NoSuchElementException noSuchElementException){
            System.out.println("Program will be finished now.");
            noSuchElementException.printStackTrace();
            System.exit(0);
        }
    }

    /** Method that checks if file exists and can be readable and writable
     * @return status of file
     */
    public boolean checkFile(String pathToFile){
        File checkingFile = new File(pathToFile);
        if(!checkingFile.exists()){
            System.out.println("File not found. Try again.");
            return false;
        }
        if(!checkingFile.canRead()){
            System.out.println("File can't be read. Try again.");
            return false;
        }
        if(!checkingFile.canWrite()){
            System.out.println("You can't write in this file. Try another.");
            return false;
        }
        System.out.println("File is valid.");
        return true;
    }

    /** Method for printing manual */
    public void help() {
        for(Map.Entry<String, String> entry : commandsInfo.entrySet()){
            System.out.println(entry.getKey() + entry.getValue());
        }
    }

    /** Method for printing information about collection */
    public void info(){
        System.out.println("Type of collection: HashMap");
        System.out.println("Initialization time: " + initializationDate);
        System.out.println("Amount of elements in the collection" + cities.size());
        System.out.println("Collection manager is active " + wasStart);
    }

    /** Method for printing all elements of the collection in string format */
    public void show(){
        for(Map.Entry<Long, City> city : cities.entrySet()){
            System.out.println(city.getValue().toString() + "\n");
        }
    }

    /** Method for receiving id
     *
     * @return long id
     */
    public long receiveID(){
        long id = 0;
        while(true){
            if(cities.containsKey(id)){
                id++;
            }
            else{
                break;
            }
        }
        return id;
    }
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
            catch (NoSuchElementException noSuchElementException){
                System.out.println("Program was stopped successfully.");
                System.exit(1);
            }
        }
    }

    public float receiveX(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.print("Enter X coordinate in a float type. Value must be greater than -944 and can't be empty.");
                float x = in.nextFloat();
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
            catch(NoSuchElementException noSuchElementException){
                System.out.println("Program was stopped successfully.");
                System.exit(1);
            }
        }
    }

    public int receiveY(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.print("Enter Y coordinate in a int type. Value can't be empty.");
                int y = in.nextInt();
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
            catch(NoSuchElementException noSuchElementException){
                System.out.println("Program was stopped successfully.");
                System.exit(1);
            }
        }
    }

    public Coordinates receiveCoordinates(){
                return new Coordinates(receiveX(), receiveY());
    }

    public LocalDateTime receiveCreationDate(){
        int year = (int) Math.floor(Math.random()*2000);
        int month = (int) Math.floor(Math.random()*12);
        int day = 1;
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = (int) Math.floor(Math.random()*31);
                break;
            case 2:
                day = (int) Math.floor(Math.random()*28);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = (int) Math.floor(Math.random()*30);
            default:
        }
        LocalDate date = LocalDate.of(year, month, day);
        LocalTime time = LocalTime.of((int) Math.floor(Math.random()* 23), (int) Math.floor(Math.random()* 59), (int) Math.floor(Math.random()* 59));
        return LocalDateTime.of(date, time);
    }

    public double receiveArea(){
        for( ; ; ) {
            try {
                System.out.println("Enter a number of area in double format. Value must be more that 0 and can't be empty.");
                Scanner in = new Scanner(System.in);
                double num = in.nextDouble();
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
            catch (NoSuchElementException noSuchElementException) {
                System.out.println("Program was stopped successfully.");
                System.exit(1);
            }
        }
    }
    public int receivePopulation(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter number of population. Value must be greater than 0 and can't be empty.");
                int pop = in.nextInt();
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
            catch(NoSuchElementException noSuchElementException){
                System.out.println("Program was stopped successfully.");
                System.exit(1);
            }
        }
    }
    public float receiveMetersAboveSeaLevel(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter meters above sea level. Value can't be empty.");
                float meters = in.nextFloat();
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
            catch(NoSuchElementException noSuchElementException){
                System.out.println("Program was stopped successfully. ");
                System.exit(1);
            }
        }
    }

    public LocalDate receiveEstablishmentDate() {
        for (; ; ) {
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
            } catch (Exception exception) {
                System.out.println("Invalid date format. Try again.");
            }
        }
    }

    public int receiveTelephoneCode(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter a telephone code. Value must be greater than 0, but lower than 100000.");
                int code = in.nextInt();
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
            catch(NoSuchElementException noSuchElementException){
                System.out.println("Program was finished successfully.");
                System.exit(1);
            }
        }
    }

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
                int number = in.nextInt();
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
            catch(NoSuchElementException noSuchElementException){
                System.out.println("Program was finished successfully.");
                System.exit(1);
            }
        }
    }
    public Human receiveGovernor(){
        for( ; ; ){
            try{
                Scanner in = new Scanner(System.in);
                System.out.println("Enter a date and time of birth with a format: yyyy-MM-dd hh:MM:ss.");
                String dateTime = in.nextLine();
                LocalDateTime birthday = LocalDateTime.parse(dateTime, formatter);
                return new Human(birthday);
            }
            catch(InputMismatchException inputMismatchException){
                System.out.println("Input value must be String format.");
            }
            catch (DateTimeException dateTimeException){
                System.out.println("Your data format is invalid. Try again.");
            }
            catch(NoSuchElementException noSuchElementException){
                System.out.println("Program was finished successfully.");
                System.exit(1);
            }
        }
    }
    /** Method for adding a new element with a key */
    public void insert(String key){
        try {
            key = key.trim();
            long id = Long.parseLong(key);
            City newCity = new City(id, receiveName(), receiveCoordinates(), receiveCreationDate(), receiveArea(),
                    receivePopulation(), receiveMetersAboveSeaLevel(), receiveEstablishmentDate(), receiveTelephoneCode(),
                    receiveClimate(), receiveGovernor());
            cities.put(id, newCity);
        }
        catch (Exception exception){
            System.out.println("Enter a long-type value. Try again.");
        }
    }

    /** Method for updating an element of collection */
    public void update_id(String key){
        try{
            key = key.trim();
            long id = Long.parseLong(key);
            cities.remove(id);
            City updatedCity = new City(id, receiveName(), receiveCoordinates(), receiveCreationDate(),
                    receiveArea(), receivePopulation(), receiveMetersAboveSeaLevel(), receiveEstablishmentDate(),
                    receiveTelephoneCode(), receiveClimate(), receiveGovernor());
            cities.put(id, updatedCity);
        }
        catch (Exception exception){
            System.out.println("Enter a long-type value. Try again.");
        }
    }

    /** Method for removing an element from the collection by a key*/
    public void remove_key (String key){
        try{
            key = key.trim();
            long id = Long.parseLong(key);
            cities.remove(id);
            System.out.println("Element with an id :" + id + " removed.");
        }catch(Exception exception){
            System.out.println("Enter a long-type value. Try again.");
        }
    }

    /** Method for clearing the collection */
    public void clear(){
        for(Map.Entry<Long, City> city : cities.entrySet()){
            cities.remove(city.getKey());
        }
        System.out.println("Collection is empty now.");
    }

    /** Method for saving the collection to a file */
    public void save(){
        try{
            Cities newCities = new Cities();
            newCities.setCities(new ArrayList<>(cities.values()));
            JAXBContext jaxbContext = JAXBContext.newInstance(Cities.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Marshal the cities list in file
            jaxbMarshaller.marshal(newCities, xmlCollection);
        }
        catch(JAXBException jaxbException){
            System.out.println("XML syntax error. Try again.");
        }
    }

    /** Method for executing from a file */
    public void execute_script (String nameOfFile){

        System.out.println("To avoid recursion, your file shouldn't contain execute_script commands.");
        File file = new File(nameOfFile);
        List<String> commands = new ArrayList<String>();
        Scanner scanner;
        try{
            scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String line = scanner.nextLine().trim();
                commands.add(line);
            }
        }
        catch(FileNotFoundException fileNotFoundException){
            System.out.println("File not available.");
        }
        String []commandsArray = (String[]) commands.toArray();

        for(String command : commandsArray){
            if(command.equals("help")) help();
            else if(command.equals("info")) info();
            else if(command.equals("show")) show();
            else if(command.toLowerCase().substring(0, 6).equals("insert"))
                if(!command.substring(6).equals(""))
                    insert(command.substring(6));
                else
                    System.out.println("Write an argument to insert.");
            else if(command.toLowerCase().substring(0, 9).equals("update_id"))
                if(!command.substring(9).equals(""))
                    update_id(command.substring(9));
                else
                    System.out.println("Write an argument to insert.");
            else if(command.toLowerCase().substring(0, 10).equals("remove_key"))
                if(!command.substring(10).equals(""))
                    remove_key(command.substring(10));
                else
                    System.out.println("Write an argument to insert.");
            else if(command.toLowerCase().substring(0, 5).equals("clear"))
                clear();
            else if(command.toLowerCase().substring(0, 4).equals("save"))
                save();
            else if(command.toLowerCase().substring(0,14).equals("execute_script"))
                System.out.println("This script can't contain this command.");
            else if(command.toLowerCase().substring(0,4).equals("exit"))
                exit();
            else if(command.toLowerCase().substring(0, 18).equals("remove_greater_key"))
                if(!command.substring(18).equals(""))
                    remove_greater_key(command.substring(18));
                else
                    System.out.println("Write an argument to insert.");
            else if(command.toLowerCase().substring(0,16).equals("remove_lower_key"))
                if(!command.substring(16).equals(""))
                    remove_lower_key(command.substring(16));
                else
                    System.out.println("Write an argument to insert.");
            else if(command.toLowerCase().substring(0, 27).equals("count_by_establishment_date"))
                count_by_establishment_date();
            else if(command.toLowerCase().substring(0, 28).equals("group_counting_by_population"))
                group_counting_by_population();
            else if(command.toLowerCase().substring(0, 34).equals("count_less_than_establishment_date"))
                count_less_than_establishment_date();
        }

    }

    /** Method for exiting without saving */
    public void exit(){
        try{
            System.out.println("Program will be finished now.");
            TimeUnit.SECONDS.sleep(3);
            System.exit(0);
        }
        catch(InterruptedException interruptedException){
            System.out.println("Program will be finished now. ");
            System.exit(0);
        }
    }

    /** Method for removing elements greater than inserted
     * not working now
     */
    public void remove_greater(int key){

    }

    /** Method for removing elements that have greater key */
    public void remove_greater_key (String k){
        try{
            k = k.trim();
            long key = Long.parseLong(k);
            for(Map.Entry<Long, City> city : cities.entrySet()){
                if(city.getKey() > key)
                    cities.remove(city.getKey());
            }
            System.out.println("Elements that has greater ID are removed.");
        }catch(Exception exception){
            System.out.println("Value must be a long-type format. Try again.");
        }

    }

    /** Method for removing elements that have lower key */
    public void remove_lower_key (String k){
        try{
            k = k.trim();
            long key = Long.parseLong(k);
            for(Map.Entry<Long, City> city : cities.entrySet()){
                if(city.getKey() < key)
                    cities.remove(city.getKey());
            }
        }catch(Exception exception){
            System.out.println("Value must be a long-type format. Try again.");
        }

    }

    /** Method for grouping by population */
    public void group_counting_by_population(){
        HashMap<Long, Long> pop = new HashMap<Long, Long>();
        pop = null;
        for(Map.Entry<Long, City> city : cities.entrySet()){
            if(!pop.containsKey(city.getKey())){
                pop.put(city.getKey(), (long) 1);
            }
            else{
                pop.put(city.getKey(),pop.get(city.getKey()) + 1);
            }
        }
        for(Map.Entry<Long, Long> population : pop.entrySet()){
            System.out.println(population.getKey() + " people live in " + population.getValue() + " towns.");
        }
    }
    /** Method for counting elements by establishment date */
    public void count_by_establishment_date (){
        Scanner in = new Scanner(System.in);
        LocalDate establishmentDate;
        LocalDate temp;
        System.out.print("Enter date in yyyy-MM-dd format: ");
        String date = in.nextLine();
        if(date.isEmpty()){
            count_by_establishment_date();
        }
        try{
            establishmentDate = LocalDate.parse(date, formatter);
            int counter = 0;
            for(Map.Entry<Long, City> city : cities.entrySet()){
                if(city.getValue().getEstablishmentDate().isEqual(establishmentDate)){
                    counter++;
                }
            }
            System.out.println("Amount of dates equaled to one you inserted: " + counter + ".");
        }
        catch(Exception e){
            System.out.println("Invalid date format.");
            count_by_establishment_date();
        }
    }

    public void count_less_than_establishment_date (){
        Scanner in = new Scanner(System.in);
        LocalDate establishmentDate;
        System.out.print("Enter date in yyyy-MM-dd format: ");
        String date = in.nextLine();
        if(date.isEmpty()){
            System.out.println("Date value can't be empty.");
            count_less_than_establishment_date();
        }
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
            establishmentDate = LocalDate.parse(date, formatter);
            int counter = 0;
            for(Map.Entry<Long, City> city : cities.entrySet()){
                if(city.getValue().getEstablishmentDate().isBefore(establishmentDate)){
                    counter++;
                }
            }
            System.out.println("Amount of dates that are before than date inserted: " + counter);
        }
        catch(Exception e){
            System.out.println("Invalid date format.");
            count_less_than_establishment_date();
        }
    }
}
