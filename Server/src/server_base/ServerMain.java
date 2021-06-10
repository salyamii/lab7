package server_base;

public class ServerMain {
    private static String path;

    public static void main(String[] args) {
        String jdbcURL = "jdbc:postgresql://localhost:4141/studs";
        String usernameDB = "pg";
        String passwordDB = "studs";
        System.out.println("Server starts running...");
        ServerUDP serverUDP = new ServerUDP(new CollectionAdministrator(
                new DatabaseHandler(jdbcURL, usernameDB, passwordDB)));
        serverUDP.run();
    }

    public static String getPath() {
        return path;
    }
}
