import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.time.ZonedDateTime;

import data.*;

/**
 * @author Shkurenko Nikita
 * @version 1.0
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
    public static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
    /** Manual of commands *new* */
    private final ArrayList<String> helper = new ArrayList<>();
    {
        wasStart = false;
        cities = new HashMap<Long, City>();
        helper.add("help - display help for available commands.");
        helper.add("info - print all information about collection.");
        helper.add("show - print all elements in collection");
        helper.add("insert - add a new element");
        helper.add("update_id {id} - upadte an element with inserted id");
        helper.add("remove_key {id} - delete an element from collection by key");
        helper.add("clear - remove all elements from collection");
        helper.add("save - saving the collection to the xml file");
        helper.add("execute_script {file_name} - executing script from the file");
        helper.add("exit - exit the program (without saving)");
        helper.add("remove_greater {population} - removing elements with greater population field");
        helper.add("remove_greater_key {id} - remove elements with greater id (key)");
        helper.add("remove_lower_key {id} - remove elements with lower id (key)");
        helper.add("group_counting_by_population - group elements by population");
        helper.add("count_by_establishment_date {establishmentDate} - display amount of elements with inserted establishment date");
        helper.add("count_less_than_establishment_date {establishmentDate} - display amount of elements that have lower value of establishmentDate");

    }

    // Constructor for checking a path to file existence and file readiness to work
    public CollectionManager(String path){
        Scanner in = new Scanner(System.in);
        try{
            for( ; ; ){
                String pathToFile;
                if(checkFile(path))
                    pathToFile = path;
                else {
                    System.out.print("Enter a full path to XML file with collection: ");
                    pathToFile = in.nextLine();
                }
                if(checkFile(pathToFile)){
                    try{
                        final QName qName = new QName("city");
                        InputStream inputStream = new FileInputStream(new File(pathToFile));

                        // create xml event reader for input stream
                        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
                        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);
                        // initialize jaxb
                        JAXBContext context = JAXBContext.newInstance(City.class);
                        Unmarshaller unmarshaller = context.createUnmarshaller();
                        XMLEvent e;

                        //Field for counting amount of downloaded elements
                        int counterGood = 0;
                        int counterBad = 0;

                        //Loop for unmarshalling collection
                        while((e = xmlEventReader.peek()) != null ) {
                            //check the event is a Document start element2
                            if(e.isStartElement() && ((StartElement) e).getName().equals(qName)){
                                //unmarshalling the document
                                City unmarshalledCity = unmarshaller.unmarshal(xmlEventReader, City.class).getValue();
                                Coordinates newCoordinates = unmarshalledCity.getCoordinates();
                                Human newHuman = unmarshalledCity.getGovernor();
                                if(unmarshalledCity.getName() != null && !unmarshalledCity.getName().equals("")
                                        && unmarshalledCity.getCoordinates() != null
                                        && unmarshalledCity.getArea() > 0 && unmarshalledCity.getPopulation() > 0
                                        && unmarshalledCity.getTelephoneCode() > 0 && unmarshalledCity.getTelephoneCode() <= 1000000
                                        && unmarshalledCity.getClimate() != null && unmarshalledCity.getGovernor() != null
                                        && newCoordinates.getX() > -944){

                                    if(unmarshalledCity.getId() == 0)
                                        unmarshalledCity.setId(receiveID());
                                    if(unmarshalledCity.getCreationDate() != null)
                                        unmarshalledCity.setCreationDate(receiveCreationDate());
                                    if(cities.containsKey(unmarshalledCity.getId()))
                                        System.out.println("The element in the collection with ID: "
                                                + unmarshalledCity.getId() + " will be replaced.");
                                    cities.put(unmarshalledCity.getId(), unmarshalledCity);
                                    counterGood++;
                                }
                                else{
                                    counterBad++;
                                }
                            } else{
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
                        System.out.println("File not found.");
                    } catch (XMLStreamException e) {
                        System.out.println("Stream exception.");
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
            System.out.println("File not found.");
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
        //System.out.println("File is valid.");
        return true;
    }

    /** Method for printing manual */
    public void help() {
        for(int i = 0; i < helper.size() ; i++){
            System.out.println(helper.get(i));
        }
    }

    /** Method for printing information about collection */
    public void info(){
        System.out.println("Type of collection: HashMap");
        System.out.println("Initialization time: " + initializationDate);
        System.out.println("Amount of elements in the collection: " + cities.size());
        System.out.println("Collection manager is active: " + wasStart);
    }

    /** Method for printing all elements of the collection in string format */
    public void show(){
        if(cities.isEmpty()){
            System.out.println("Collection is empty.");
            return;
        }
        System.out.println("All elements of the collection: ");
        for(Map.Entry<Long, City> city : cities.entrySet()){
            System.out.println(city.getValue().toString() + "\n");
        }
    }

    /** Method for receiving id
     *
     * @return long id
     */
    public long receiveID(){
        long id = 1;
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

    /**
     * Method for receiving Coordinate-type field
     * @return Coordinates coordinates
     */
    public Coordinates receiveCoordinates(){
        return new Coordinates(receiveX(), receiveY());
    }

    /**
     * Method for receiving creation date
     * @return LocalDateTime creation date
     */
    public LocalDateTime receiveCreationDate(){
        int year = (int) Math.floor(Math.random()*1000 + 1000);
        int month = (int) Math.floor(Math.random()*11+1);
        int day = 1;
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = (int) Math.floor(Math.random()*30+1);
                break;
            case 2:
                day = (int) Math.floor(Math.random()*27+1);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = (int) Math.floor(Math.random()*29+1);
                break;
            default:
                break;
        }
        LocalDate date = LocalDate.of(year, month, day);
        LocalTime time = LocalTime.of((int) Math.floor(Math.random()* 23), (int) Math.floor(Math.random()* 59), (int) Math.floor(Math.random()* 59));
        return LocalDateTime.of(date, time);
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
        }
    }

    /**
     * Method for receiving establishment date
     * @return LocalDate establishmentDate
     */
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
            } catch (IllegalArgumentException illegalArgumentException) {
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
    /** Method for adding a new element with a key */
    public void insert(){
        long id = receiveID();
        City newCity = new City(id, receiveName(), receiveCoordinates(), receiveCreationDate(), receiveArea(),
                receivePopulation(), receiveMetersAboveSeaLevel(), receiveEstablishmentDate(), receiveTelephoneCode(),
                receiveClimate(), receiveGovernor());
        cities.put(id, newCity);
        System.out.println("A new city with " + id + " ID was inserted.");
    }

    /** Method for updating an element of collection */
    public void update_id(String key){
        try{
            key = key.trim();
            long id = Long.parseLong(key);
            if(!cities.containsKey(id)){
                System.out.println("There is no element with this ID in collection. Try another ID.");
                return;
            }
            City updatedCity = cities.get(id);
            updatedCity.setCreationDate(receiveCreationDate());
            updatedCity.setArea(receiveArea());
            updatedCity.setClimate(receiveClimate());
            updatedCity.setCoordinates(receiveCoordinates());
            updatedCity.setEstablishmentDate(receiveEstablishmentDate());
            updatedCity.setGovernor(receiveGovernor());
            updatedCity.setMetersAboveSeaLevel(receiveMetersAboveSeaLevel());
            updatedCity.setPopulation(receivePopulation());
            updatedCity.setTelephoneCode(receiveTelephoneCode());
            updatedCity.setName(receiveName());
            cities.put(id, updatedCity);
            System.out.println("City with " + id + " id was updated.");
        }
        catch (NumberFormatException numberFormatException){
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
        }catch(NumberFormatException numberFormatException){
            System.out.println("Enter a long-type value. Try again.");
        }
    }

    /** Method for clearing the collection */
    public void clear(){
        cities.clear();
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
            System.out.println("The collection was saved successfully!");
        }
        catch(JAXBException jaxbException){
            System.out.println("XML syntax error. Try again.");
        }
    }

    /** Method for executing from a file */
    public void execute_script (String nameOfFile){
        try{
            File file = new File(nameOfFile);
            Scanner sc = new Scanner(file);
            String []finalCommand;
            String command;
            if(!recursionDetector(nameOfFile)){
                while(sc.hasNextLine()){
                    command = sc.nextLine();
                    finalCommand = command.trim().toLowerCase().split(" ", 2);
                    try {
                        switch (finalCommand[0]) {
                            case "help":
                                help();
                                break;
                            case "info":
                                info();
                                break;
                            case "show":
                                show();
                                break;
                            case "insert":
                                insert();
                                break;
                            case "update_id":
                                update_id(finalCommand[1]);
                                break;
                            case "remove_key":
                                remove_key(finalCommand[1]);
                                break;
                            case "clear":
                                clear();
                                break;
                            case "execute_script":
                                execute_script(finalCommand[1]);
                                break;
                            case "exit":
                                exit();
                            case "remove_greater":
                                remove_greater(finalCommand[1]);
                                break;
                            case "remove_greater_key":
                                remove_greater_key(finalCommand[1]);
                                break;
                            case "remove_lower_key":
                                remove_lower_key(finalCommand[1]);
                                break;
                            case "group_counting_by_population":
                                group_counting_by_population();
                                break;
                            case "count_by_establishment_date":
                                count_by_establishment_date();
                                break;
                            case "count_less_than_establishment_date":
                                count_less_than_establishment_date();
                            default:
                                System.out.println("Unknown command. Check help for information.");
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
                        System.out.println("Argument of command is absent. Check help for information.");
                    }
                }
            }
            else{
                System.out.println("This script is recursive! Fix the script or use another one.");
            }
        }
        catch(FileNotFoundException noSuchElementException){
            System.out.println("File can't be read. Program will be finished.");
            System.exit(1);
        }
    }

    /** Method for exiting without saving */
    public void exit(){
        try{
            System.out.println("Program will be finished now.");
            TimeUnit.SECONDS.sleep(1);
            System.exit(0);
        }
        catch(InterruptedException interruptedException){
            System.out.println("Program will be finished now. ");
            System.exit(0);
        }
    }

    /** Method for removing elements greater than inserted
     *
     */
    public void remove_greater(String strPopulation){
        try{
            if(strPopulation.equals("")){
                System.out.println("Value population can't be empty.");
                return;
            }
            int populationMax = Integer.parseInt(strPopulation);
            ArrayList<Long> greaterPopulationsIds = new ArrayList<>();

            for(Map.Entry<Long, City> city : cities.entrySet()){
                if(city.getValue().getPopulation() > populationMax){
                    greaterPopulationsIds.add(city.getValue().getId());
                }
            }
            System.out.println("Elements that have greater population value will be removed. Amount: " + greaterPopulationsIds.size() + ".");
            for(int i = 0; i < greaterPopulationsIds.size(); i++){
                cities.remove(greaterPopulationsIds.get(i));
            }
        }
        catch(NumberFormatException numberFormatException){
            System.out.println("Value population must be int-type.");
        }

    }

    /** Method for removing elements that have greater key */
    public void remove_greater_key (String k){
        try{
            k = k.trim();
            ArrayList<Long> keys = new ArrayList<>();
            long key = Long.parseLong(k);
            for(Map.Entry<Long, City> city : cities.entrySet()){
                if(city.getKey() > key)
                    keys.add(city.getKey());
            }
            System.out.println("Cities with greater keys will be removed. Amount of cities with greater keys: " + keys.size() + ".");
            for(int i = 0; i < keys.size(); i++) {
                cities.remove(keys.get(i));
            }
        }catch(NumberFormatException numberFormatException){
            System.out.println("Value must be a long-type format. Try again.");
        }

    }

    /** Method for removing elements that have lower key */
    public void remove_lower_key (String k){
        try{
            k = k.trim();
            ArrayList<Long> keys = new ArrayList<>();
            long key = Long.parseLong(k);
            for(Map.Entry<Long, City> city : cities.entrySet()){
                if(city.getKey() < key)
                    keys.add(city.getKey());
            }
            System.out.println("Cities with lower keys will be deleted. Amount of cities with lower keys: " + keys.size() + ".");
            for(int i = 0; i < keys.size(); i++) {
                cities.remove(keys.get(i));
            }
        }catch(NumberFormatException numberFormatException){
            System.out.println("Value must be a long-type format. Try again.");
        }

    }

    /** Method for grouping by population */
    public void group_counting_by_population(){
        HashMap<Long, Long> pop = new HashMap<Long, Long>();
        for(Map.Entry<Long, City> city : cities.entrySet()){
            if(pop.get(city.getKey()) == null){
                long key = city.getKey();
                pop.put(key, (long) 1);
            }
            else{
                System.out.println(city.getValue() + " " + city.getKey());
                pop.put(city.getKey(),pop.get(city.getKey()) + 1);
            }
        }
        if(pop.entrySet().isEmpty()){
            System.out.println("There are no cities in collection.");
            return;
        }
        for(Map.Entry<Long, Long> population : pop.entrySet()){
            System.out.println(population.getKey() + " people live in " + population.getValue() + " town(s).");
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
    HashSet<String> suspectedFiles = new HashSet<>();
    public boolean recursionDetector(String path) throws FileNotFoundException{
        if(!checkFile(path)) {
            System.out.println("Script with path: " + path + " is unavailable.");
            return false;
        }
        File file = new File(path);
        Scanner in = new Scanner(file);
        String []script_path;
        String str;
        while(in.hasNextLine()){
            str = in.nextLine();
            script_path = str.trim().toLowerCase().split(" ", 2);
            if(script_path[0].equals("execute_script") && suspectedFiles.contains(script_path[1])) {
                return true;
            }
            else if(script_path[0].equals("execute_script")){
                suspectedFiles.add(script_path[1]);
                recursionDetector(script_path[1]);
            }
        }
        return false;
    }
}


