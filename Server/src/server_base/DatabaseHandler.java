package server_base;

import data.City;
import data.Climate;
import data.Coordinates;
import data.Human;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static server_base.CollectionAdministrator.dateTimeFormatter;

public class DatabaseHandler {

    private static final String ADD_USER_REQUEST = "INSERT INTO users(username, password) VALUES (?, ?)";
    private static final String VALIDATE_USER_REQUEST = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
    private static final String FIND_USERNAME_REQUEST = "SELECT COUNT(*) FROM users WHERE username = ?";
    private static final String INSERT_CITY_REQUEST = "INSERT INTO cities " +
            "(id, name, coordinates_x, coordinates_y," +
            " creation_date, area, population, meters_above_sea_level, " +
            "establishment_date, telephone_code, climate, governor, owner)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CHECK_ID_PRESENT_REQUEST = "SELECT COUNT (*) AS count FROM cities WHERE id = ?";
    private static final String IS_OWNER_REQUEST = "SELECT COUNT(*) FROM cities WHERE id = ? AND owner = ?";
    private static final String REMOVE_BY_KEY_REQUEST = "DELETE FROM cities WHERE id = ?";
    private static final String UPDATE_CITIES_BY_ID_REQUEST = "UPDATE cities SET name = ?," +
            " coordinates_x = ?," +
            "coordinates_y = ?," +
            "area = ?," +
            "population = ?," +
            "meters_above_sea_level = ?," +
            "establishment_date = ?," +
            "telephone_code = ?," +
            "climate = ?," +
            "governor = ? WHERE ? = id";

    //private static final String IF_OWNER_REQUEST = " SELECT COUNT(*) FROM cities WHERE owner = ?";

    private String URL;
    private String usernameDB;
    private String passwordDB;
    private Connection connection;

    public DatabaseHandler(String URL, String usernameDB, String passwordDB){
        this.URL = URL;
        this.usernameDB = usernameDB;
        this.passwordDB = passwordDB;
    }

    public void connectToDatabase(){
        try{
            connection = DriverManager.getConnection(URL, usernameDB, passwordDB);
            System.out.println("Connection to the database established.");
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
            System.err.println("Can not establish the connection to the database.");
            System.exit(-1);

        }
    }


    /**
     * Method for loading data from database initially
     * @return tempCities
     */
    public HashMap<Long, data.City> loadCollection(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ResultSet rs = null;
        try {
            HashMap<Long, data.City> tempCities = new HashMap<>();
            connectToDatabase();
            if (!getConnection().isClosed()){

                PreparedStatement joinStatement = getConnection().prepareStatement("SELECT * FROM cities");
                rs = joinStatement.executeQuery();
                while(rs.next()){//add validation of inputs
                    City newCity = new City((long) rs.getInt("id"),
                            rs.getString("name"),
                            new data.Coordinates(rs.getFloat("coordinates_x"), rs.getInt("coordinates_y")),
                            LocalDateTime.parse(rs.getString("creation_date"), formatter),
                            rs.getDouble("area"),
                            rs.getInt("population"),
                            rs.getFloat("meters_above_sea_level"),
                            LocalDate.parse(rs.getString("establishment_date"), formatter2),
                            rs.getInt("telephone_code"),
                            Climate.valueOf(rs.getString("climate")),
                            new Human(LocalDateTime.parse(rs.getString("governor"), dateTimeFormatter)));
                    tempCities.put(newCity.getId(), newCity);
                }
                joinStatement.close();
                System.out.println("The collection from database was loaded to the memory.");
                 return tempCities;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("Can not connect to database.");
            System.exit(-1);
        }
        return null;
    }

    public Connection getConnection(){
        return connection;
    }

     public boolean insertCity(City city, String owner){
         try {

             connection.setAutoCommit(false);
             connection.setSavepoint();
             Coordinates coordinates = city.getCoordinates();

             PreparedStatement insertCityStatement = connection.prepareStatement(INSERT_CITY_REQUEST);
             insertCityStatement.setLong(1,city.getId());
             insertCityStatement.setString(2, String.valueOf(city.getName()));
             insertCityStatement.setFloat(3,coordinates.getX());
             insertCityStatement.setInt(4, coordinates.getY());
             insertCityStatement.setString(5, city.getCreationDateString());
             insertCityStatement.setDouble(6, city.getArea());
             insertCityStatement.setInt(7, city.getPopulation());
             insertCityStatement.setFloat(8, city.getMetersAboveSeaLevel());
             insertCityStatement.setString(9, city.getEstablishmentDateString());
             insertCityStatement.setInt(10, city.getTelephoneCode());
             insertCityStatement.setString(11, city.getClimate().name());
             insertCityStatement.setString(12, city.getGovernor().getBirthdayString());
             insertCityStatement.setString(13, owner);

             insertCityStatement.executeUpdate();
             insertCityStatement.close();

             connection.commit();
             connection.setAutoCommit(true);

             return true;
         } catch (SQLException sqlException) {
             sqlException.printStackTrace();
             try {
                 connection.setAutoCommit(true);
                 connection.rollback();

             } catch (SQLException throwables) {
                 System.out.println("Could not return to the previous step.");
             }
         }
         return false;
     }

     public boolean updateCity(City city, String owner){
         try {
             if(!isOwnerOf(city.getId(), owner)) return false;

             connection.setAutoCommit(false);
             connection.setSavepoint();
             Coordinates coordinates = city.getCoordinates();

             PreparedStatement updateCities = connection.prepareStatement(UPDATE_CITIES_BY_ID_REQUEST);
             updateCities.setString(1,city.getName());
             updateCities.setFloat(2,coordinates.getX());
             updateCities.setInt(3, coordinates.getY());
             updateCities.setDouble(4, city.getArea());
             updateCities.setInt(5,  city.getPopulation());
             updateCities.setFloat(6, city.getMetersAboveSeaLevel());
             updateCities.setString(7, city.getEstablishmentDateString());
             updateCities.setInt(8, city.getTelephoneCode());
             updateCities.setString(9, city.getClimate().name());
             updateCities.setString(10, city.getGovernor().getBirthdayString());
             updateCities.setLong(11, city.getId());

             updateCities.executeUpdate();
             updateCities.close();

             connection.commit();
             connection.setAutoCommit(true);

             return true;
         } catch (SQLException sqlException) {
             sqlException.printStackTrace();
             try {
                 connection.setAutoCommit(true);
                 connection.rollback();

             } catch (SQLException throwables) {
                 System.out.println("Could not return to the previous step.");
             }
         }
         return false;
     }

     public boolean removeCityByID(long id, String possibleOwner) throws SQLException {
        PreparedStatement checkIfIdExist = connection.prepareStatement(CHECK_ID_PRESENT_REQUEST);
        checkIfIdExist.setLong(1, id);
        ResultSet resultSet = checkIfIdExist.executeQuery();
        resultSet.next();
        if(resultSet.getInt(1) != 0 && isOwnerOf(id, possibleOwner)){
            PreparedStatement cityStatement = connection.prepareStatement(REMOVE_BY_KEY_REQUEST);
            connection.setAutoCommit(false);
            connection.setSavepoint();

            cityStatement.setLong(1, id);

            cityStatement.executeUpdate();
            cityStatement.close();

            connection.commit();
            connection.setAutoCommit(true);
            return true;
        }
        else return false;
     }
     public boolean isOwnerOf(long id, String possibleOwner) throws SQLException{
        PreparedStatement ownerStatement = connection.prepareStatement(IS_OWNER_REQUEST);
        ownerStatement.setLong(1, id);
        ownerStatement.setString(2, possibleOwner);
        ResultSet resultSet = ownerStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1) == 1;
     }

     public boolean checkID(long id) throws SQLException{
        PreparedStatement check = connection.prepareStatement(CHECK_ID_PRESENT_REQUEST);
        check.setLong(1, id);
        ResultSet resultSet = check.executeQuery();
        resultSet.next();
        return resultSet.getInt(1) != 0;
     }

     public boolean validateUser(String username, String password) throws SQLException{
        PreparedStatement validateUserStatement = connection.prepareStatement(VALIDATE_USER_REQUEST);
        String hashedPassword = password; // need to add hashing algorithm
         validateUserStatement.setString(1, username);
         validateUserStatement.setString(2, hashedPassword);
         ResultSet resultSet = validateUserStatement.executeQuery();
         resultSet.next();
         if(resultSet.getInt(1) == 1){
             validateUserStatement.close();
             return true;
         }
         else{
             validateUserStatement.close();
             return false;
         }
     }

     public boolean registerUser(String username, String password) throws SQLException {
        if(!ifUserExists(username)){
            PreparedStatement registerUserStatement = connection.prepareStatement(ADD_USER_REQUEST);
            String hashedPassword = password; // need to add hashing algorithm
            registerUserStatement.setString(1, username);
            registerUserStatement.setString(2, hashedPassword);
            registerUserStatement.executeUpdate();
            registerUserStatement.close();
            return true;
        }
        else return false;
     }

     public boolean ifUserExists(String username) throws SQLException{
        PreparedStatement findUserStatement = connection.prepareStatement(FIND_USERNAME_REQUEST);
        findUserStatement.setString(1, username);
        ResultSet resultSet = findUserStatement.executeQuery();
        resultSet.next();
        if(resultSet.getInt(1) == 1){
            findUserStatement.close();
            return true;
        }
        else{
            findUserStatement.close();
            return false;
        }
     }
}
