package com.example.mischa.tasten_neigung;

import android.os.HandlerThread;
import android.os.Handler;

/**
 * Created by sturzer on 23.06.17.
 */

public class TelemetryHandlerThread extends HandlerThread {

    private Handler handler;

    public TelemetryHandlerThread(String name) {
        super(name);
    }

    public void postTask(Runnable task){
        handler.post(task);
    }

    public void prepareHandler(){
        handler = new Handler(getLooper());
    }
}