package server_base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;

public class DatabaseHandler {

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
            System.err.println("Can not establish the connection to the database.");
            System.exit(-1);

        }
    }

    public Connection getConnection(){
        return connection;
    }
}
