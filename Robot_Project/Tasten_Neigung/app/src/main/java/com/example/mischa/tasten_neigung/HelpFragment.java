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
 * Class HelpFragment to show help
 */

public class HelpFragment extends Fragment {

    // listener to communicate with parent
    ExitHelpInterface listener;
    // exit
    ImageButton exitbutton;

    /**
     * default constructor
     */
    public HelpFragment() {
    }

    /**
     * Method onAttach to register Listener to parent
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ExitManualInterface) {
            listener = (ExitHelpInterface) context;
        } else {
            throw new ClassCastException(context.toString() + "Parent must implement exitHelpInterface");
        }
    }

    /**
     * Class onCreateView to inflate layout and register exitbutton
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the setted view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View helpView =  inflater.inflate(R.layout.help_page, container, false);

        exitbutton = (ImageButton) helpView.findViewById(R.id.exitHelp);
        exitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.exitHelpFragment();
            }
        });

        return helpView;
    }

}

