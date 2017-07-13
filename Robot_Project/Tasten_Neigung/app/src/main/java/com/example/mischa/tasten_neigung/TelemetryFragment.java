package com.example.mischa.tasten_neigung;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan Turzer on 25.06.17.
 */

public class TelemetryFragment extends Fragment {

    ExitTelemetryInterface listener;
    ImageButton exitbutton;
    TextView speedview, voltview;

    RadarChart radar;
    List<RadarEntry> radarList;

    public TelemetryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ExitManualInterface) {
            listener = (ExitTelemetryInterface) context;
        } else {
            throw new ClassCastException(context.toString() + "Parent must implement ExitManualInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View telemetryView =  inflater.inflate(R.layout.telemetry_main, container, false);

        exitbutton = (ImageButton) telemetryView.findViewById(R.id.exitTelemetry);
        exitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.exitTelemetryFragment();
            }
        });

        speedview = (TextView) telemetryView.findViewById(R.id.speed);
        speedview.setBackgroundColor(Color.TRANSPARENT);

        voltview = (TextView) telemetryView.findViewById(R.id.voltage);
        voltview.setBackgroundColor(Color.TRANSPARENT);

        radar = (RadarChart) telemetryView.findViewById(R.id.radarChart);

        // radar configuration
        radar.setRotationEnabled(false);

        Legend l = radar.getLegend();
        l.setEnabled(false);

        Description d = radar.getDescription();
        d.setText("Sonar Range Finder");

        XAxis xaxis = radar.getXAxis();
        xaxis.setEnabled(true);

        YAxis yaxis = radar.getYAxis();
        yaxis.setEnabled(true);

        List<RadarEntry> radarList = new ArrayList<>();
        for(int i = 0; i < 16; i++) {
            radarList.add(new RadarEntry(0f));
        }
        RadarDataSet radar_set = new RadarDataSet(radarList, "Sonar");

        radar_set.setFillColor(10);

        radar.setData(new RadarData(radar_set));
        radar.invalidate();

        return telemetryView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Callback Subscriber für EventBus Nachrichten
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TelemetryMessage event) {
        // Float singe Stats
        speedview.setText(event.getSpeed().toString());
        voltview.setText(event.getVolt().toString());

        // Radar Readings zufügen
        List<Float> tmp = event.getMeasurements();
        List<RadarEntry> radarList = new ArrayList<>();
        for(int i = 0; i < tmp.size(); i++) {
            radarList.add(new RadarEntry(tmp.get(i).floatValue()));
        }
        RadarDataSet radar_set = new RadarDataSet(radarList, "Sonar");
        radar_set.setDrawValues(false);
        radar_set.setDrawIcons(true);
        radar.setData(new RadarData(radar_set));
        //radar.setRotation(180.0f / radarList.size());
        radar.invalidate();
    }

}
