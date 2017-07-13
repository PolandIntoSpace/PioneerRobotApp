package com.example.mischa.tasten_neigung;

import android.widget.ImageButton;

import org.junit.Test;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

import static org.junit.Assert.*;

/**
 * @author Michael Reupold
 */

public class UnitTests {

    /**
     * Data_Frame_Buttons_used test to test all 64 possible pressable combinations on for steering
     * buttons on correct creation of the steering message.
     *
     * @throws Exception
     */
    @Test
    public void data_Frame_Buttons_used() throws Exception {
        DataFrame dataFrame = new DataFrame();

        String intensity;
        String front;
        String right;
        String back;
        String left;
        String assertionText;
        String intensityMessage = "";
        String message;

        byte driveDirection;
        byte rotateDirection = 0;

        for (int intensitySlide = 0; intensitySlide <= 4; intensitySlide++) {

            switch (intensitySlide) {
                case 0:
                    intensityMessage = "001";
                    break;
                case 1:
                    intensityMessage = "010";
                    break;
                case 2:
                    intensityMessage = "011";
                    break;
                case 3:
                    intensityMessage = "100";
                    break;
                case 4:
                    intensityMessage = "101";
            }
            dataFrame.setIntensity(intensitySlide);

            intensity = "intensity = " + (intensitySlide + 1) + ", ";

            for (int frontButton = 0; frontButton < 2; frontButton++) {

                if (frontButton != 0) {
                    dataFrame.setUpButton(true);

                } else {
                    dataFrame.setUpButton(false);

                }

                front = "front = " + frontButton + ", ";

                for (int rightButton = 0; rightButton < 2; rightButton++) {

                    if (rightButton != 0) {
                        dataFrame.setRightButton(true);
                    } else {
                        dataFrame.setRightButton(false);
                    }

                    right = "right = " + rightButton + ", ";

                    for (int backButton = 0; backButton < 2; backButton++) {

                        if (backButton != 0) {
                            driveDirection = 1;
                            dataFrame.setDownButton(true);

                        } else {
                            dataFrame.setDownButton(false);
                            driveDirection = 0;
                        }

                        back = "back = " + backButton + ", ";

                        for (int leftButton = 0; leftButton < 2; leftButton++) {

                            if (leftButton != 0) {
                                dataFrame.setLeftButton(true);
                                rotateDirection = 0;
                            } else {
                                dataFrame.setLeftButton(false);
                                rotateDirection = 1;
                            }

                            left = "left = " + leftButton + ", ";

                            assertionText = intensity + front + right + back + left;

                            byte[] messageFromDataFrame = dataFrame.getMessage();

                            message = Integer.toBinaryString(messageFromDataFrame[0] & 0xFF);

                            if (message.length() < 8) {

                                String zeros = "";
                                for (int count = 0; count < (8 - message.length()); count++) {
                                    zeros = zeros + "0";
                                }
                                message = zeros + message;
                            }

                            //check intensity drive
                            if (((backButton == 0) && (frontButton == 0)) || ((backButton != 0) && (frontButton != 0))) {
                                assertTrue(assertionText + " intensity drive wrong", "000".equals(message.substring(5, 8)));
                            } else {
                                assertTrue(assertionText + " intensity drive wrong", intensityMessage.equals(message.substring(5, 8)));
                            }

                            //check intensity rotation
                            if (((rightButton == 0) && (leftButton == 0)) || ((rightButton != 0) && (leftButton != 0))) {
                                assertTrue(assertionText + " intensity rotation wrong", "000".equals(message.substring(2, 5)));
                            } else {
                                assertTrue(assertionText + " intensity rotation wrong", intensityMessage.equals(message.substring(2, 5)));
                            }

                            //check direction drive

                            if (!((backButton == 0) && (frontButton == 0)) && !((backButton != 0) && (frontButton != 0))) {
                                assertTrue(assertionText + " drive direction wrong", Integer.toBinaryString(driveDirection).equals(message.substring(1, 2)));
                            }

                            //check direction rotation
                            if (!((rightButton == 0) && (leftButton == 0)) && !((rightButton != 0) && (leftButton != 0))) {
                                int tempRotateDirection = rotateDirection;
                                if (driveDirection == 1) {
                                    if (tempRotateDirection == 1) {
                                        tempRotateDirection = 0;
                                    } else {
                                        tempRotateDirection = 1;
                                    }
                                }
                                assertTrue(assertionText + " rotation direction wrong", Integer.toBinaryString(tempRotateDirection).equals(message.substring(0, 1)));
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * Test Class for Orientation sensor
     * @throws Exception
     */
    @Test
    public void data_Frame_Orientation_sensor_used() throws Exception {
        DataFrame dataFrame = new DataFrame();
        float[] orientation = new float[2];
        String message;
        String intensityDrive;
        String intensityRotation;
        String directionDrive;
        String directionRotation;

        for (int roll = -90; roll <= 90; roll++) {
            for (int pitch = -90; pitch <= 90; pitch++) {
                for (int azimut = 0; azimut <= 360; azimut++) {
                    orientation[0] = (float)(Math.sin(Math.toRadians(azimut)) * pitch);
                    orientation[1] = (float)(Math.cos(Math.toRadians(azimut)) * roll);

                    dataFrame.inputOrientationSensor(orientation);
                    byte[] messageFromDataFrame = dataFrame.getMessage();
                    message = Integer.toBinaryString(messageFromDataFrame[0] & 0xFF);

                    if (message.length() < 8) {

                        String zeros = "";
                        for (int count = 0; count < (8 - message.length()); count++) {
                            zeros = zeros + "0";
                        }
                        message = zeros + message;
                    }



                    if (orientation[0] > 5) {
                        directionDrive = "1";
                    } else {
                        directionDrive = "0";
                    }

                    if (orientation[1] > 0) {
                        directionRotation = "0";
                    } else {
                        directionRotation = "1";
                    }

                    if (orientation[0] > 5) {
                        if (directionRotation.equals("1")){
                            directionRotation = "0";
                        }
                        else {
                            directionRotation = "1";
                        }
                    }


                    if (Math.abs(orientation[0]) < 5) {
                        intensityDrive = "000";
                    } else if (Math.abs(orientation[0]) < 15) {
                        intensityDrive = "001";
                    } else if (Math.abs(orientation[0]) < 25) {
                        intensityDrive = "010";
                    } else if (Math.abs(orientation[0]) < 35) {
                        intensityDrive = "011";
                    } else if (Math.abs(orientation[0]) < 45) {
                        intensityDrive = "100";
                    } else {
                        intensityDrive = "101";
                    }

                    if (Math.abs(orientation[1]) < 5) {
                        intensityRotation = "000";
                    } else if (Math.abs(orientation[1]) < 15) {
                        intensityRotation = "001";
                    } else if (Math.abs(orientation[1]) < 25) {
                        intensityRotation = "010";
                    } else if (Math.abs(orientation[1]) < 35) {
                        intensityRotation = "011";
                    } else if (Math.abs(orientation[1]) < 45) {
                        intensityRotation = "100";
                    } else {
                        intensityRotation = "101";
                    }

                    assertTrue(" intensity drive wrong, pitch = " + orientation[0] + " roll angle: " + orientation[1]
                             + " given: " + message.substring(5, 8) + " expected: " + intensityDrive, intensityDrive.equals(message.substring(5, 8)));
                    assertTrue(" intensity rotation wrong, pitch = " + orientation[0] + " roll angle: " + orientation[1]
                            + " given: " + message.substring(2, 5) + " expected: " + intensityRotation, intensityRotation.equals(message.substring(2, 5)));
                    if(!(intensityDrive.equals("000") && !(intensityRotation.equals("000")))) {
                        assertTrue(" drive direction wrong, pitch = " + orientation[0] + " roll angle: " + orientation[1]
                                + " given: " + message.substring(1, 2) + " expected: " + directionDrive, directionDrive.equals(message.substring(1, 2)));
                        assertTrue(" rotation direction wrong, pitch " + orientation[0] + " roll angle: " + orientation[1]
                                + " given: " + message.substring(0, 1) + " expected: " + directionRotation, directionRotation.equals(message.substring(0, 1)));
                    }
                }
            }
        }
    }

    @Test
    public void Udp_Client_Steering_test() throws Exception {
        UdpClient_Steering udpClient = new UdpClient_Steering();
        udpClient.openSocket("127.0.0.1", 8844);
        byte[] message;

        UDP_Server server = new UDP_Server();
        server.receiveData();

        message = ByteBuffer.allocate(1).put((byte)164).array();
        for (int i = 0; i < 100; i++) {
            udpClient.sendMessage(message);
        }
        byte[] receivedMessage = server.getMessage();
        assertTrue(Arrays.equals(message, receivedMessage));
    }

}


