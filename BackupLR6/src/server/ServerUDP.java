package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerUDP extends Thread{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public ServerUDP(){
        try{
            socket = new DatagramSocket(4445);
        }
        catch (SocketException socketException){
            System.out.println("Something wrong with socket you chosen.");
        }
    }

    public void run (){
        running = true;
        while(running){
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try{
                socket.receive(packet);
            }
            catch (IOException ioException){
                System.out.println("Invalid Object received.");
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());

            if(received.equals("exit")){
                running = false;
                continue;
            }
            try{
                socket.send(packet);
            }
            catch(IOException ioException){
                System.out.println("Invalid Object sent.");
            }

        }
        socket.close();
    }
}
