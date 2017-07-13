package com.example.mischa.tasten_neigung;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * @author Michael Reupold
 * Class Manual Fragment to show user manual
 */

public class ManualFragment extends Fragment{

    //listener for communication to parent
    ExitManualInterface listener;
    //exit
    ImageButton exitbutton;

    /**
     * default constructor
     */
    public ManualFragment() {
    }

    /**
     * Method onAttach to register Listener
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ExitManualInterface) {
            listener = (ExitManualInterface) context;
        } else {
            throw new ClassCastException(context.toString() + "Parent must implement ExitManualInterface");
        }
    }

    /**
     * Method onCreateView to inflate Layout and register exitbutton
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the setted View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View manualView =  inflater.inflate(R.layout.steering_manual, container, false);

        exitbutton = (ImageButton) manualView.findViewById(R.id.exitManual);
        exitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.exitManualFragment();
            }
        });

        return manualView;
    }


}