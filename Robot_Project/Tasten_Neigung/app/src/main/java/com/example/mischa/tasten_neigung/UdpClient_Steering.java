package com.example.mischa.tasten_neigung;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Michael Reupold
 * Class UdpClient_Steering
 * client for udp service
 */
public class UdpClient_Steering {
    DatagramSocket socket;
    InetAddress IPAddress;
    int PORT;

    /*
     * default constructor
     * @param IPADDRESS
     * @param PORT
     */
     UdpClient_Steering() {
    }

    /**
     * Method openSocket
     * @param IPADDRESS
     * @param PORT
     */
    public void openSocket(String IPADDRESS, int PORT){
        try {
            IPAddress = InetAddress.getByName(IPADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.PORT = PORT;

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sender for udp message
     *
     * @param message: as the message you want to send
     */
    public void sendMessage(final byte[] message) {

        //thread to send udp message
        Thread sendUdp = new Thread(new Runnable() {

            @Override
            public void run() {
                DatagramPacket pack = new DatagramPacket(message, message.length, IPAddress, PORT);
                try {
                    socket.send(pack);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        sendUdp.start();

    }

    /**
     * Mehtod close socket
     */
    public void closeSocket(){
        socket.close();
    }

}


