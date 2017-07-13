package com.example.mischa.tasten_neigung;

/**
 * @author Michael Reupold
 * Interface ExitManualInterface for exithelpFragment
 */

public interface ExitSoundInterface {

    /**
     * Method to communicate exit order from Fragment and Parent
     */
    public void exitSoundInterface();
    /**
     * Method to communicate changed honking sound from Fragment and Parent
     */
    public void setSound(int auswahl);
}
