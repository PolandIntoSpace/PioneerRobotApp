package com.example.mischa.tasten_neigung;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Michael Reupold
 * Dummy UDP-Receiver for Testing
 */

public class UDP_SERVER_TEST {

    private byte[] message = null;
    private byte[] zero = ByteBuffer.allocate(1).put((byte)0).array();
    private byte[] zeroMessage = null;
    Thread receiveUDP;
    DatagramSocket serverSocket = null;
    boolean finishThread = false;

    UDP_SERVER_TEST(){

        receiveUDP = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] receiveData = new byte[1];

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    serverSocket.receive(receivePacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!Arrays.equals(receivePacket.getData(), zero)){
                    message = receivePacket.getData();
                }
                else {
                    zeroMessage = receivePacket.getData();
                }
                if (!finishThread) {
                    run();
                }

            }
        });
    }

    public void receiveData() throws IOException {
        finishThread = false;
        message = null;
        zero = ByteBuffer.allocate(1).put((byte)0).array();
        zeroMessage = null;

        try {
            serverSocket = new DatagramSocket(8844);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        receiveUDP.start();
    }

    public byte[] getMessage() throws InterruptedException {
        finishThread = true;
        receiveUDP.join();
        serverSocket.close();
        if (message != null) {
            return message;
        }
        else if (zeroMessage != null) {
            return zeroMessage;
        }
        else {
            return null;
        }
    }
}
