package client;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("Starting..");
        ClientUDP clientUDP = new ClientUDP();
        clientUDP.sendClient();
    }
}
