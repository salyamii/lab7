package server_base;

public class ServerMain {
    private static String path;

    public static void main(String[] args) {
        String jdbcURL = "jdbc:postgresql://pg:5432/studs";
        String usernameDB = "********";
        String passwordDB = "********";
        try{
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException classNotFoundException){
            System.err.println("PostgreSQL driver has not found.");
            System.exit(-1);
        }
        DatabaseHandler dh = new DatabaseHandler(jdbcURL, usernameDB, passwordDB);
        CollectionAdministrator ca = new CollectionAdministrator(dh);
        dh.connectToDatabase();
        ca.init();
        System.out.println("Server starts running...");
        ServerUDP serverUDP = new ServerUDP(ca);
        serverUDP.run();
    }
}
