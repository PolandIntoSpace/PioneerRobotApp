package com.example.mischa.tasten_neigung;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioGroup;

/**
 * @author Aleksandra Targowicki
 * @author Michael Reupold (bug fixing)
 *
 * Class Sound Fragment for changing honking sounds.
 */

public class SoundFragment extends Fragment {

    //Listener to Interface for communication with parent
    ExitSoundInterface listener;
    //exit
    ImageButton exitbutton;
    //selection
    RadioGroup radioGroup;

    /**
     * Default constructor
     */
    public SoundFragment() {
    }

    /**
     * onAttach to register context to listener
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ExitSoundInterface) {
            listener = (ExitSoundInterface) context;
        } else {
            throw new ClassCastException(context.toString() + "Parent must implement exitSoundInterface");
        }
    }

    /**
     * Method onCreateView to create events on the radiobuttons and set their listener.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return updated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate activity_sound
        View soundView =  inflater.inflate(R.layout.activity_sound, container, false);

        exitbutton = (ImageButton) soundView.findViewById(R.id.exitSound);
        //set listener for exit button
        exitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.exitSoundInterface();
            }
        });

        //set different selections for honking sounds
        radioGroup = (RadioGroup) soundView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View radio = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radio);

                switch (index) {
                    case 0:
                        listener.setSound(0);
                        break;
                    case 1:
                        listener.setSound(1);
                        break;
                    case 2:
                        listener.setSound(2);
                        break;
                    case 3:
                        listener.setSound(3);
                        break;
                }
            }
        });


        return soundView;
    }
}