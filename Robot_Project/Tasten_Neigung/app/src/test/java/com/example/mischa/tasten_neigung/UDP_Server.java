package com.example.mischa.tasten_neigung;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author Michael Reupold
 * Dummy receiver for Testing
 */

public class UDP_Server {

    private byte[] message;
    Thread receiveUDP;

    UDP_Server(){
        receiveUDP = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket serverSocket = null;
                try {
                    serverSocket = new DatagramSocket(8844);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                byte[] receiveData = new byte[1];

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    serverSocket.receive(receivePacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                message = receivePacket.getData();
            }
        });
    }

    public void receiveData() throws IOException {
        receiveUDP.start();
    }

    public byte[] getMessage() throws InterruptedException {
        receiveUDP.join();
        return message;
    }
}
