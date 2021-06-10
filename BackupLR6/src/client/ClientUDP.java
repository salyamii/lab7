package client;

import java.io.IOException;
import java.net.*;

public class ClientUDP {
    private DatagramSocket socket;
    private InetAddress address;

    private byte [] buf;

    public ClientUDP(){
        try{
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        }
        catch(UnknownHostException unknownHostException){
            System.out.println("Can't find IP. Fix it.");
        }
        catch (SocketException socketException){
            System.out.println("The socket could not be opened, or the socket could not bind to the specified local port.");
        }
    }

    public String sendClient (String msg){
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        try{
            socket.send(packet);
        }
        catch (IOException ioException){
            System.out.println("Packet can't be sent.");
        }
        packet = new DatagramPacket(buf, buf.length);
        try{
            socket.receive(packet);
        }
        catch (IOException ioException){
            System.out.println("Packet can't be received.");
        }
        String received  = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close(){
        socket.close();
    }
}
