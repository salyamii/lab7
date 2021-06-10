package server;

import server.data.*;
import server.utility_methods.IsFileValid;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.time.*;
import java.util.*;
import java.io.*;
import java.time.format.DateTimeFormatter;

public class CollectionAdministrator {
    private final HashMap<Long, server.data.City> cities = new HashMap<>();
    private File xmlCollection;

    private ZonedDateTime initializationDate;

    private boolean running = false;

    public static final String DATE_FORMATTER = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    public CollectionAdministrator(String path) {
        try{
            if(IsFileValid.run(path)){
                try{
                    final QName startElementName = new QName("city");
                    InputStream inputStream = new FileInputStream(new File(path));
                    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
                    XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);
                    JAXBContext jaxbContext = JAXBContext.newInstance(City.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    XMLEvent event;
                    while((event = xmlEventReader.peek())!= null){
                        if (event.isStartElement() && ((StartElement) event).getName().equals(startElementName)){
                            City parsedCity = unmarshaller.unmarshal(xmlEventReader, City.class).getValue();
                            Coordinates parsedCityCoordinates = parsedCity.getCoordinates();
                            if(parsedCity.getName() != null && !parsedCity.getName().equals("")
                                    && parsedCity.getCoordinates() != null
                                    && parsedCity.getArea() > 0 && parsedCity.getPopulation() > 0
                                    && parsedCity.getTelephoneCode() > 0 && parsedCity.getTelephoneCode() <= 1000000
                                    && parsedCity.getClimate() != null && parsedCity.getGovernor() != null
                                    && parsedCityCoordinates.getX() > -944) {
                                if(parsedCity.getId() == 0)
                                    parsedCity.setId(receiveID());
                                if(parsedCity.getCreationDate() != null)
                                    parsedCity.setCreationDate(receiveCreationDate());
                                if(cities.containsKey(parsedCity.getId()))
                                    System.out.println("The element in the collection with ID: "
                                            + parsedCity.getId() + " will be replaced.");
                                cities.put(parsedCity.getId(), parsedCity);
                            }
                        }
                        else{
                            xmlEventReader.nextEvent();
                        }
                    }
                    System.out.println("Collection is loaded.");
                    running = true;
                    xmlCollection = new File(path);
                    initializationDate = ZonedDateTime.now();
                }
                catch (JAXBException jaxbException){
                    System.out.println("An error was encountered while creating the Unmarshaller object");
                }
                catch (XMLStreamException xmlStreamException){
                    System.out.println("Can't peek an element from file.");
                }
            }
            else{
                System.out.println("File is not valid.");
            }
        }
        catch (FileNotFoundException fileNotFoundException){
            System.out.println("File not found.");
        }
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

    /**
     * Method for printing manual
     */
    public void help() {
        for(int i = 0; i < helper.size() ; i++){
            System.out.println(helper.get(i));
        }
    }

    /**
     * Getter for helper array
     * @return
     */
    public ArrayList<String> getHelper(){
        return helper;
    }

    /**
     * Method for printing information about collection
     */
    public void info(){
        System.out.println("Type of collection: HashMap");
        System.out.println("Initialization time: " + initializationDate);
        System.out.println("Amount of elements in the collection: " + cities.size());
        System.out.println("Collection manager is active: " + running);
    }

    /**
     * Getter for info about collection
     * @return
     */
    public String getInfo(){
        return "Type of collection: HashMap\nInitialization time and date" + initializationDate +
                "\nAmount of elements in the collection: " + cities.size() +"\nCollection manager is active: " + running;
    }

    /**
     * Method for printing all elements of the collection in string format
     */
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

    public String getShow(){
        if(cities.isEmpty()){
            return "Collection is empty.\n";
        }
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<Long, City> city : cities.entrySet()){
            builder.append(city.getValue().toString() + "\n");
        }
        return builder.toString();
    }
    /**
     * Add element
     */
    public void insert(){
        long id = receiveID();
        City newCity = new City(id, receiveName(), receiveCoordinates(), receiveCreationDate(), receiveArea(),
                receivePopulation(), receiveMetersAboveSeaLevel(), receiveEstablishmentDate(), receiveTelephoneCode(),
                receiveClimate(), receiveGovernor());
        cities.put(id, newCity);
        System.out.println("A new city with " + id + " ID was inserted.");
    }

    /**
     * Update element by key
     * @param key
     */
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

    /**
     * Removing by key
     * @param key
     */
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

    /**
     * Deleting all elements from collection
     */
    public void clear(){
        cities.clear();
        System.out.println("Collection is empty now.");
    }

    /** Method for saving the collection to a file */
    /**
     * Saving a collection.
     */
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
    /**
     * Execute from a file
     * @param nameOfFile
     */
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

    /**
     * Method for exiting without saving
     */
    public void exit(){
        System.out.println("Program will be finished now.");
        System.exit(0);
    }

    /**
     * Method for removing elements greater than inserted
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

    /**
     * Method for removing elements that have greater key
     */
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

    /**
     * Method for removing elements that have lower key
     */
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

    /**
     * Method for grouping by population
     */
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
    /**
     * Method for counting elements by establishment date
     */
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

    /**
     * Counting less values of establishment date
     */
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

    /**
     * Recursion detector
     * @return boolean
     */
    HashSet<String> suspectedFiles = new HashSet<>();
    public boolean recursionDetector(String path) throws FileNotFoundException{
        if(!IsFileValid.run(path)) {
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

    public HashMap<Long, City> getCities() {
        return cities;
    }
}
