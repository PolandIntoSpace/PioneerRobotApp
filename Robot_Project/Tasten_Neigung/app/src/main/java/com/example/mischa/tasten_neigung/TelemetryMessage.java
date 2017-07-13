package com.example.mischa.tasten_neigung;

import java.util.List;

/**
 * Diese Felder werden an das Telemetry Fragment geschickt um dort angezeigt zu werden
 *
 * @author Stefan Turzer
 * Created by Stefan Turzer on 26.06.17.
 */

public class TelemetryMessage {
    private final Float speed, volt;
    private final List<Float> measurements;

    public TelemetryMessage(Float speed, Float volt, List<Float> measurements) {
        this.speed = speed;
        this.volt = volt;
        this.measurements = measurements;
    }

    public Float getSpeed() {
        return speed;
    }
    public Float getVolt() {
        return volt;
    }
    public List<Float> getMeasurements() { return measurements; }
}
