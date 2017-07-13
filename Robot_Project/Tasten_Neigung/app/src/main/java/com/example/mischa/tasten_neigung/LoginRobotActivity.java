package com.example.mischa.tasten_neigung;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author Aleksandra Targowicki
 * edited: Michael Reupold
 *
 * Purpose: Manage login screen and NFC.
 *
 * NFC Reader based on this code example:
 * http://www.codexpedia.com/android/android-nfc-read-and-write-example/
 */


public class LoginRobotActivity extends AppCompatActivity {

    public static final String IPADDRESS = "ipadress";
    public static final String TAG = LoginRobotActivity.class.getSimpleName();
    static String message;

    private String ipadress = "10.10.0."; // set address


    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_robot);

        // Changed to work with Espresso Testing. Edited by Michael Reupold
        Button button = (Button) findViewById(R.id.button_login);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectToRobot(v);
            }
        });


        //setFont();
        setAddress(ipadress);
        setNFCinfo(NfcAdapter.getDefaultAdapter(this), (TextView) findViewById(R.id.text_nfc), (ImageView) findViewById(R.id.image_nfc));

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
    }

    /**
     * Change font of text to look more fancy (deactivated at the end)
     */

    private void setFont() {
        EditText mEditText = (EditText) findViewById(R.id.editText_login);
        TextView txtNfc = (TextView) findViewById(R.id.text_nfc);
        TextView txtIp = (TextView) findViewById(R.id.text_ip);
        Button btn = (Button) findViewById(R.id.button_login);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/Thin_Pencil_Handwriting.ttf");

        mEditText.setTypeface(custom_font, Typeface.BOLD);
        txtNfc.setTypeface(custom_font, Typeface.BOLD);
        txtIp.setTypeface(custom_font, Typeface.BOLD);
        btn.setTypeface(custom_font, Typeface.BOLD);
    }

    /**
     * Help user by showing part of the IP.
     * Cursor has to be set manually at the end, otherwise it will be always show at the beginning.
     *
     * @param address IP address that will be shown at the start of the application on the login screen
     */
    private void setAddress(String address) {
        EditText mEditText = (EditText) findViewById(R.id.editText_login);
        mEditText.setText(address);
        int pos = mEditText.getText().length();
        mEditText.setSelection(pos);
    }


    /**
     * Show on the device if NFC is supported:
     * yes -> show picture + text + start Intent
     * no  -> show picture faded + faded text
     *
     * @param adapter (mostly) default adapter of the device
     * @param tv change this textview properties
     * @param iv change this imageview properties
     */
    private void setNFCinfo(NfcAdapter adapter, TextView tv, ImageView iv) {
        if (adapter == null) {
            tv.setText(getString(R.string.loginNFCoff));
            tv.setAlpha((float) 0.20);
            iv.setAlpha((float) 0.20);
            return;
        } else {
            tv.setText(getString(R.string.loginNFCon));
            tv.setAlpha((float) 1);
            iv.setAlpha((float) 1);
            readFromIntent(getIntent());
        }
    }

    /**
     * The button to establish a connection with the robot was pressed (login screen).
     *
     * @param v start from this view
     */
    protected void connectToRobot(View v) {
        EditText mEditText = (EditText) findViewById(R.id.editText_login);
        message = mEditText.getText().toString();
        if (checkIP(message)) {
            SharedPreferences prefs = this.getSharedPreferences(
                    "com.example.mischa.tasten_neigung", Context.MODE_PRIVATE);
            prefs.edit().putString("ipaddress",message).commit();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(IPADDRESS, message);
            startActivity(intent);
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.loginErrorIP, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Method checkIP
     *
     * @param ip string representation of IP-Address
     * @return wether ip is valid or not
     */
    // added by Michael Reupold
    public boolean checkIP(String ip){

        String [] domain = ip.split ("\\.");
        if (domain.length != 4){
            return false;
        }
        for (String temp : domain){

            try {
                Integer.parseInt(temp);
                int i = Integer.parseInt (temp);
                if (i < 0 || i > 255)
                {
                    return false;
                }
            }
            catch (NumberFormatException e) {
                return false;
            }


        }
        return true;
    }

    /******************************************************************************
     ******************************Read From NFC Tag********************************
     ******************************************************************************/

    /**
     * Check if an intent in combination with NFC appeared.
     * If so try to read the NFC tag.
     *
     * @param intent detected NFC intent
     */
    private void readFromIntent(Intent intent) {

        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }


    /**
     * Read the text from the tag and show it in the textfield.
     * If the text is longer than the max set number of digits ignore it and show a toast.
     *
     * @param msgs data from NFC tag
     */
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        try {
            text = new String(payload);
            if (text.length() > 15) { // IP Adress: 192.168.2.xx
                throw new Exception();
            } else {
                EditText mEditText = (EditText) findViewById(R.id.editText_login);
                mEditText.setText(text);
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.loginFalseTag, Toast.LENGTH_LONG).show();
        }
    }
}

