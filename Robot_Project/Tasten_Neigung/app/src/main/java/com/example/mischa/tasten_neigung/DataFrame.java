package com.example.mischa.tasten_neigung;

import android.support.v7.app.AppCompatActivity;

import java.nio.ByteBuffer;

/**
 * @author Michael Reupold
 * Class Data Frame manages the Data that is sended
 */

// Message Overview
// bit 1-3 intensity drive axis (0 to 5)
// bit 4-6 intensity rotation axis (0 to 5)
// bit 7 direction drive axis (0 = forward, 1 = backward)
// bit 8 direction rotation axis (0 = left, 1 = right)

public class DataFrame extends AppCompatActivity {

    private boolean upButton = false;
    private boolean rightButton = false;
    private boolean downButton = false;
    private boolean leftButton = false;
    private int[] intensity = new int[2]; // 0 = drive axis, 1 = rotation axis
    private byte[] message = new byte[2];


    /**
     * setter for up button/drive forward
     *
     * @param pressed
     */
    public void setUpButton(boolean pressed) {
        upButton = pressed;
        // to update the current steering message
        setMessage();
    }

    /**
     * setter for right button/turn right
     *
     * @param pressed
     */
    public void setRightButton(boolean pressed) {
        rightButton = pressed;
        // to update the current steering message
        setMessage();
    }

    /**
     * setter for down button/back up
     *
     * @param pressed
     */
    public void setDownButton(boolean pressed) {
        downButton = pressed;
        // to update the current steering message
        setMessage();
    }

    /**
     * setter for left button/turn left
     *
     * @param pressed
     */
    public void setLeftButton(boolean pressed) {
        leftButton = pressed;
        // to update the current steering message
        setMessage();
    }

    /**
     * setter for intensity(key driving mode only)
     *
     * @param intensity
     */
    public void setIntensity(int intensity) {
        // driving intensity and rotation intensity ind key driving mode is set simultaniously
        this.intensity[0] = intensity + 1;
        this.intensity[1] = intensity + 1;
        // to update the current steering message
        setMessage();
    }

    /**
     * method to set data of orientation sensor
     *
     * @param orientation
     */
    public void inputOrientationSensor(float[] orientation) {

        //if pitch is greater than five, backward driving is required
        //value is set to five because driving backward will invert the turning process
        //for more ic_help see comment in setMessage()
        if (orientation[0] > 5) {
            downButton = true;
            upButton = false;
        } else {
            upButton = true;
            downButton = false;
        }

        //in case roll is greater than zero, the robot will turn left.
        if (orientation[1] > 0) {
            leftButton = true;
            rightButton = false;
        } else {
            rightButton = true;
            leftButton = false;
        }

        // Overview of intensities in angle range:
        // 0 - 5 stop
        // 5 - 15 speed 1
        // 15 - 25 speed 2
        // 25 - 35 speed 3
        // 35 - 45 speed 4
        // 45 + x speed 5

        //set intensity according to angle of pitch (for forward and backward driving, Index 0) and
        //for roll (for left and right turn, Index 1)
        //intensity for driving and rotation can have different values
        for (int index = 0; index < 2; index++) {

            if (Math.abs(orientation[index]) < 5) {
                intensity[index] = 0;
            } else if (Math.abs(orientation[index]) < 15) {
                intensity[index] = 1;
            } else if (Math.abs(orientation[index]) < 25) {
                intensity[index] = 2;
            } else if (Math.abs(orientation[index]) < 35) {
                intensity[index] = 3;
            } else if (Math.abs(orientation[index]) < 45) {
                intensity[index] = 4;
            } else {
                intensity[index] = 5;
            }
        }
        //to update message
        setMessage();
    }

    /**
     * setter for the message
     * composes the message that will be sent
     */
    // Message Overview
    // bit 1-3 intensity drive axis (0 to 5)
    // bit 4-6 intensity rotation axis (0 to 5)
    // bit 7 direction drive axis (0 = forward, 1 = backward)
    // bit 8 direction rotation axis (0 = left, 1 = right)
    private void setMessage() {

        int drive = intensity[0];
        int rotation = intensity[1];

        byte composeMessage = 0;

        // case no forward movement
        if (!upButton && !downButton) {
            drive = 0;
        }

        // case up and down are pressed simultaniously
        if (upButton && downButton) {
            drive = 0;
        }

        // case no turning required
        if (!rightButton && !leftButton) {
            rotation = 0;
        }

        // case left and right are pressed simultaniously
        if (rightButton && leftButton) {
            rotation = 0;
        }


        // Backward and turning required. In this case, the turning will be inverted due
        // to behaviour of backward driving of a single axis vehicle.
        // after invertion the robot will have the simulated behaviour of a vehicle with two
        // axes like a car.
        // if not inverted, the vehicle will e.g. at a message of backward and left move backward
        // and turn simultaniously left on the vehicles zenith axis. this will result in the vehicle
        // driving backward and turning right on its track
        if (leftButton && downButton) {
            //set bit 8 for turning
            composeMessage = (byte) (composeMessage | 1);
        } else if (rightButton && !downButton) {
            //set bit 8 for turning
            composeMessage = (byte) (composeMessage | 1);
        }

        //shift to next
        composeMessage = (byte) (composeMessage << 1);

        //set bit 7 for driving direction
        if (downButton) {
            composeMessage = (byte) (composeMessage | 1);
        }
        // shift to next
        composeMessage = (byte) (composeMessage << 3);

        //set bits 4 to 6 for rotation intensity
        composeMessage = (byte) (composeMessage | rotation);

        //shift to next
        composeMessage = (byte) (composeMessage << 3);

        //set bits 1 to 3 for driving intensity
        composeMessage = (byte) (composeMessage | drive);

        //allocate bits to byte array
        message = ByteBuffer.allocate(1).put(composeMessage).array();
    }

    /**
     * Getter for the Message
     *
     * @return message
     */
    public byte[] getMessage() {
        return message;
    }
}
