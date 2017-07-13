package com.example.mischa.tasten_neigung;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * @author Michael Reupold
 * Class Orientation
 * handles the orientation sensor
 */
public class Orientation implements SensorEventListener {

    private SensorManager sensorManager;
    private float gravitationRot[]; //gravitation rotation matrix
    private float magneticRot[]; //magnetic rotation matrix
    private float accelerationSensor[] = new float[3];
    private float magnetSensor[] = new float[3];
    private float[] values = new float[3];

    private float[] orientation = new float[2];

    /**
     * Constructor setting context
     *
     * @param context
     */
    Orientation(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * Method to register Listener
     */
    public void register() {

        //Register acceleration sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        //Register magnetic field sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * method to unregister the listener
     */
    public void unregister() {
        sensorManager.unregisterListener(this);
    }

    /**
     * Accuracy Changed, does nothing so far
     *
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * onSensorChanged sets values if changed
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetSensor = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accelerationSensor = event.values.clone();
                break;
        }
        calcOrientation(event);
    }

    /**
     * calcOrientation sensor calculates roll and pitch angle
     * Mobile is in a Landscape layout B-Frame Nadir System
     * @param event
     */
    private void calcOrientation(SensorEvent event) {
        if (magnetSensor != null && accelerationSensor != null) {
            gravitationRot = new float[9];
            magneticRot = new float[9];
            //calculate rotation sensor
            SensorManager.getRotationMatrix(gravitationRot, magneticRot, accelerationSensor, magnetSensor);
            float[] outGravity = new float[9];
            //change to fitting coordinate system
            SensorManager.remapCoordinateSystem(gravitationRot, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, outGravity);
            //calculate roll and pitch
            SensorManager.getOrientation(outGravity, values);

            orientation[0] = values[1] * 57.2957795f; //pitch
            orientation[1] = values[2] * 57.2957795f; //roll
            magnetSensor = null;
            accelerationSensor = null;
        }
    }

    /**
     * getOrientation getter for orientation
     *
     * @return orientation
     */
    public float[] getOrientation() {
        return orientation;
    }
}
