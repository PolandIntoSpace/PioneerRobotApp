package com.example.mischa.tasten_neigung;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.view.View;
import android.widget.Switch;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

// Button Resource "https://icons8.com/web-app/42775/Back"
// Background Resource  http://www.freeware-android.net/google-android-4-2-device-2913/themes-wallpapers-skins-tag/galaxy-s5-background-water-touch-lwp-download-307419.html


/**
 * @Author Michael Reupold
 * @Author Stefan Turzer
 * @Author Aleksandra Targowicki
 * main activity for application
 */


public class MainActivity extends AppCompatActivity

implements NavigationView.OnNavigationItemSelectedListener, ExitManualInterface, ExitHelpInterface, ExitSoundInterface, ExitTelemetryInterface, ExitVideoInterface {

    //Udp Sender for Steering messages
    private UdpClient_Steering networkSevice = new UdpClient_Steering();

    private String IPADDRESS = "127.0.0.1";

    private int PORT = 8844;
    int serverPort = 8845;
    private int hupePort = 8846;

    // Handler for telemetry receiver
    private Handler telemetryHandler = new Handler();
    private TelemetryHandlerThread telemetryThread;

    private FragmentManager fragmentManager;


    private DataFrame message = new DataFrame();
    private Switch changeSteeringSensor;
    private boolean steeringSensor = false; //false = Tastensteuerung, true = Neigungssensor
    private SeekBar intensitySlider;

    private ImageButton up;
    private ImageButton right;
    private ImageButton down;
    private ImageButton left;
    private ImageButton soundBt;
    private int iSound = 0;
    private UdpClient_Steering soundTransmitter = new UdpClient_Steering();

    private Orientation orientationSensor;

    //handler for timerRunnable Thread
    private Handler timerHandler = new Handler();

    //looping thread to read orientation sensor, get message and send message
    Runnable timerRunnable = new Runnable() {
        float[] orientation = new float[2];

        /**
         * run to compose message for orientation sensor
         * and grab and send me
         * @author Michael Reupold
         */
        @Override
        public void run() {
            if (steeringSensor) {
                // get data from orientation sensor
                orientation = orientationSensor.getOrientation();
                // orientation sensor data to dataFrame
                message.inputOrientationSensor(orientation);

                //set alpha of button for not pressed
                up.setAlpha(0.5f);
                down.setAlpha(0.5f);
                right.setAlpha(0.5f);
                left.setAlpha(0.5f);

                //set alpha to simulate press
                if (orientation[0] < -5) {
                    up.setAlpha(1f);
                }
                if (orientation[0] > 5) {
                    down.setAlpha(1f);
                }
                if (orientation[1] < -5) {
                    right.setAlpha(1f);
                }
                if (orientation[1] > 5) {
                    left.setAlpha(1f);
                }
            }

            // send data to robot
            networkSevice.sendMessage(message.getMessage());
            //callback for loop
            timerHandler.postDelayed(this, 100);
        }
    };

    // UDP Receiver Thread for Performance Data
    final Runnable telemetryRunnable = new Runnable() {
        @Override
        public void run() {

            try {
                DatagramSocket s = new DatagramSocket(serverPort);
                boolean running = true;
                while (running) {
                    // Buffer
                    byte[] buf = new byte[1024];
                    DatagramPacket p = new DatagramPacket(buf, buf.length);
                    // Blocking receive
                    s.receive(p);
                    try {
                        String abc = new String(p.getData(),0,p.getLength());
                        JSONObject j = new JSONObject(abc);
                        Log.e("Server", "Got a thing" + abc.toString() );

                        // Here we will read contents
                        Float bat = Float.valueOf( (float) j.getDouble("battery") );
                        Float spd = Float.valueOf( (float) j.getDouble("speed") );

                        JSONArray ja = j.getJSONArray("messung");
                        List<Float> measurements = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            measurements.add(Float.valueOf(ja.optString(i)));
                        }

                        Log.i("Server", measurements.toString());

                        EventBus.getDefault().post(new TelemetryMessage(spd, bat, measurements));

                        // Log.i("Server", bat.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Catch wrong JSON here
                    }
                    // Refresh Data or something
                    telemetryHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ;
                        }
                    });
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * onCreate Method
     *
     * @param savedInstanceState
     * @author Michael Reupold
     * @author Aleksandra Targowicki (mostly sound, otherwise commented)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * edited Aleksandra Targowicki
         * Prevent the screen from turning dark.
         * Problem seen on an Android 7.1.2
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        /**
         * edited Aleksandra Targowicki
         * Use the IP from the login screen.
         */
        Intent intent = getIntent();
        if (intent.getStringExtra(LoginRobotActivity.IPADDRESS) != null) {
            this.IPADDRESS = intent.getStringExtra(LoginRobotActivity.IPADDRESS);
        }


        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        //set drawerLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //setNavigationview
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        //upbutton
        up = (ImageButton) findViewById(R.id.arrowUp);
        //alpha of button to not pressed
        up.setAlpha(0.5f);
        up.setBackgroundColor(Color.TRANSPARENT);
        //rightbutton
        right = (ImageButton) findViewById(R.id.arrowRight);
        //alpha of button to not pressed
        right.setAlpha(0.5f);
        right.setBackgroundColor(Color.TRANSPARENT);
        //downbutton
        down = (ImageButton) findViewById(R.id.arrowDown);
        //alpha of button to not pressed
        down.setAlpha(0.5f);
        down.setBackgroundColor(Color.TRANSPARENT);
        //leftbutton
        left = (ImageButton) findViewById(R.id.arrowLeft);
        //alpha of button to not pressed
        left.setAlpha(0.5f);
        left.setBackgroundColor(Color.TRANSPARENT);

        soundBt = (ImageButton) findViewById(R.id.btHupe);
        //alpha of button to not pressed
        soundBt.setAlpha(0.5f);
        soundBt.setBackgroundColor(Color.TRANSPARENT);

        //switch for steering sensor
        changeSteeringSensor = (Switch) findViewById(R.id.neigungssensorSwitch);
        changeSteeringSensor.setChecked(false);
        //intensity slider
        intensitySlider = (SeekBar) findViewById(R.id.intensitySlider);

        message.setIntensity(intensitySlider.getProgress());

        //register listener
        setUp();
        setRight();
        setLeft();
        setDown();
        soundTransmitter.openSocket(IPADDRESS, hupePort);
        useSound();
        useNeigungssensor();
        intensityChanged();

        networkSevice.openSocket(IPADDRESS, PORT);
        orientationSensor = new Orientation(this);
        fragmentManager = getSupportFragmentManager();

        // Start Telemetry Thread
        telemetryThread = new TelemetryHandlerThread("telemetryThread");

        telemetryThread.start();
        telemetryThread.prepareHandler();
        telemetryThread.postTask(telemetryRunnable);
    }


    /**
     * setUp set listener for up button
     * @author Michael Reupold
     */
    private void setUp() {

        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //interrupt handler callbacks
                    timerHandler.removeCallbacks(timerRunnable);
                    //set message
                    message.setUpButton(true);
                    //restart handler
                    timerHandler.postDelayed(timerRunnable, 0);
                    //set alpha to pressed
                    v.setAlpha(1f);


                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //interrupt handler callbacks
                    timerHandler.removeCallbacks(timerRunnable);
                    //set message
                    message.setUpButton(false);
                    //restart handler
                    timerHandler.postDelayed(timerRunnable, 0);
                    //set alpha to not pressed
                    v.setAlpha(0.5f);
                }
                return true;
            }
        });
    }

    /**
     * setRight set listener for right button
     * @author Michael Reupold
     */
    private void setRight() {

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //interrupt handler callbacks
                    timerHandler.removeCallbacks(timerRunnable);
                    //set message
                    message.setRightButton(true);
                    //restart handler
                    timerHandler.postDelayed(timerRunnable, 0);
                    //set alpha to pressed
                    v.setAlpha(1f);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //interrupt handler callbacks
                    timerHandler.removeCallbacks(timerRunnable);
                    //set message
                    message.setRightButton(false);
                    //restart handler
                    timerHandler.postDelayed(timerRunnable, 0);
                    //set alpha to not pressed
                    v.setAlpha(0.5f);
                }
                return true;
            }
        });

    }

    /**
     * setdown set listener for down button
     * @author Michael Reupold
     */
    private void setDown() {

        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //interrupt handler callbacks
                    timerHandler.removeCallbacks(timerRunnable);
                    //set message
                    message.setDownButton(true);
                    //restart handler
                    timerHandler.postDelayed(timerRunnable, 0);
                    //set alpha to pressed
                    v.setAlpha(1f);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //interrupt handler callbacks
                    timerHandler.removeCallbacks(timerRunnable);
                    //set message
                    message.setDownButton(false);
                    //restart handler
                    timerHandler.postDelayed(timerRunnable, 0);
                    //set alpha to not pressed
                    v.setAlpha(0.5f);
                }
                return true;
            }
        });

    }

    /**
     * setLeft set listener for left button
     * @author Michael Reupold
     */
    private void setLeft() {

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //interrupt handler callbacks
                    timerHandler.removeCallbacks(timerRunnable);
                    //set message
                    message.setLeftButton(true);
                    //restart handler
                    timerHandler.postDelayed(timerRunnable, 0);
                    //set alpha to pressed
                    v.setAlpha(1f);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //interrupt handler callbacks
                    timerHandler.removeCallbacks(timerRunnable);
                    //set message
                    message.setLeftButton(false);
                    //restart handler
                    timerHandler.postDelayed(timerRunnable, 0);
                    //set alpha to not pressed
                    v.setAlpha(0.5f);
                }
                return true;
            }
        });

    }

    /**
     * intensity changed listener for seek bar
     * @author Michael Reupold
     */
    private void intensityChanged() {
        intensitySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            /**
             * onProgressChanged for changed setting in seek bar
             * @param seekBar
             * @param progress
             * @param fromUser
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //interrupt handler callbacks
                timerHandler.removeCallbacks(timerRunnable);
                //set message
                message.setIntensity(progress);
                //restart handler
                timerHandler.postDelayed(timerRunnable, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * use Neigungssensor for change from key to orientation sensor steering
     * @author Michael Reupold
     */
    private void useNeigungssensor() {
        changeSteeringSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //interrupt handler callbacks
                    timerHandler.removeCallbacks(timerRunnable);
                    // hide intensity slider
                    intensitySlider.setVisibility(View.GONE);
                    steeringSensor = true;
                    //restart handler
                    timerHandler.postDelayed(timerRunnable, 0);
                } else {
                    //interrupt handler callbacks
                    timerHandler.removeCallbacks(timerRunnable);
                    //set message
                    message.setUpButton(false);
                    //set message
                    message.setDownButton(false);
                    //set message
                    message.setLeftButton(false);
                    //set message
                    message.setRightButton(false);
                    //set button to not pressed
                    up.setAlpha(0.5f);
                    down.setAlpha(0.5f);
                    right.setAlpha(0.5f);
                    left.setAlpha(0.5f);
                    //on return set to zero
                    intensitySlider.setVisibility(View.VISIBLE);
                    intensitySlider.setProgress(0);
                    steeringSensor = false;
                    //restart handler
                    timerHandler.postDelayed(timerRunnable, 0);
                }
            }
        });
    }



    /**
     * @author Aleksandra Targowicki
     * Send sound-ID to the UDP server on the robot (computer or RasPi).     *
     */
    private void useSound() {
        soundBt.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Byte message = 0;
                    message = (byte) (message | iSound);
                    byte[] sendmessage = ByteBuffer.allocate(1).put(message).array();
                    soundTransmitter.sendMessage(sendmessage);
                    //set alpha to pressed
                    soundBt.setAlpha(1f);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundBt.setAlpha(0.5f);
                }
                return true;
            }
        });
    }




    /**
     * onSaveInstanceState
     *
     * @param outstate
     * @author Michael Reupold
     */
    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
    }

    /**
     * onRestoreInstanceState
     *
     * @param savedInstanceState
     * @author Michael Reupold
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * onPause stop handler and unregister orientation sensor
     * @author Michael Reupold
     */
    @Override
    public void onPause() {
        super.onPause();
        //interrupt handler callbacks
        timerHandler.removeCallbacks(timerRunnable);
        orientationSensor.unregister();
        soundTransmitter.closeSocket();
        networkSevice.closeSocket();

    }

    /**
     * onResume register orientation sensor
     * @author Michael Reupold
     */
    @Override
    protected void onResume() {
        super.onResume();
        orientationSensor.register();
        changeSteeringSensor.setChecked(false);
        soundTransmitter.openSocket(IPADDRESS, hupePort);
        networkSevice.openSocket(IPADDRESS, PORT);
    } 

    /**
     * Manage Nagigation view
     * @param item is the menue
     * @return allways true
     * @author Michael Reupold
     * edited by Aleksandra Targowicki (changed hard coded strings)
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        ActionBar actionBar = getSupportActionBar();

        switch(item.getItemId()) {

            // go back to login
            case R.id.nav_login:
                startActivity(new Intent(this, LoginRobotActivity.class));
                break;

            // got to manual
            case R.id.nav_steering_manual:
                //interrupt handler callbacks
                timerHandler.removeCallbacks(timerRunnable);
                //change to manual steering
                changeSteeringSensor.setChecked(false);
                Fragment manualFragment = new ManualFragment();
                //replace fragment
                fragmentManager.beginTransaction().replace(R.id.content_frame, manualFragment).addToBackStack(null).commit();
                actionBar.setTitle(R.string.menu_manual);
                break;

            // to to help
            case R.id.nav_help:
                //interrupt handler callbacks
                timerHandler.removeCallbacks(timerRunnable);
                //change to manual steering
                changeSteeringSensor.setChecked(false);
                Fragment helpFragment = new HelpFragment();
                //replace fragment
                fragmentManager.beginTransaction().replace(R.id.content_frame, helpFragment).addToBackStack(null).commit();
                actionBar.setTitle(R.string.menu_help);
                break;

            // to to change sound
            case R.id.nav_sound:
                //interrupt handler callbacks
                timerHandler.removeCallbacks(timerRunnable);
                //change to manual steering
                changeSteeringSensor.setChecked(false);
                Fragment soundFragment = new SoundFragment();
                //replace fragment
                fragmentManager.beginTransaction().replace(R.id.content_frame, soundFragment).addToBackStack(null).commit();
                actionBar.setTitle(R.string.menu_sound);
                break;

            // Uwe
            case R.id.video:
                timerHandler.removeCallbacks(timerRunnable);
                Fragment videoFragment = new VideoFragment();
                fragmentManager.beginTransaction().replace(R.id.content_frame, videoFragment).addToBackStack(null).commit();
                actionBar.setTitle(R.string.menu_video);
                break;
                
            case R.id.telemetry:
                timerHandler.removeCallbacks(timerRunnable);
                Fragment telemetryFragment = new TelemetryFragment();
                fragmentManager.beginTransaction().replace(R.id.content_frame, telemetryFragment).addToBackStack(null).commit();
                actionBar.setTitle(R.string.menu_telemetry);
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method exitManualFragment
     * implemented method of ExitManualInterface for communication from fragment
     * @author Michael Reupold
     * edited by Aleksandra Targowicki (changed hard coded strings)
     */
    @Override
    public void exitManualFragment() {
        ManualFragment manualFragment = (ManualFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        ActionBar actionBar = getSupportActionBar();

        //remove if existing
        if (manualFragment != null) {
            fragmentManager.beginTransaction().remove(manualFragment).commitAllowingStateLoss();
            //reset title
            actionBar.setTitle(R.string.app_name);
        }
    }

    /**
     * Method exitHelpFragment
     * implemented method of ExitHelpInterface for communication from fragment
     * @author Michael Reupold
     */
    @Override
    public void exitHelpFragment() {
        ActionBar actionBar = getSupportActionBar();
        HelpFragment helpFragment = (HelpFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        //remove if existing
        if (helpFragment != null) {
            fragmentManager.beginTransaction().remove(helpFragment).commitAllowingStateLoss();
            //reset title

            actionBar.setTitle(R.string.app_name);
        }
    }

    /**
     * Method exitSoundFragment.
     * Implemented method of ExitSoundInterface for communication from fragment, based on other fragments implemented by M.Reupold.
     * @author Aleksandra Targowicki
     */
    @Override
    public void exitSoundInterface() {
        ActionBar actionBar = getSupportActionBar();
        SoundFragment soundFragment = (SoundFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
        //remove if existing
        if (soundFragment != null) {
            fragmentManager.beginTransaction().remove(soundFragment).commitAllowingStateLoss();
            //reset title
            actionBar.setTitle(R.string.app_name);
        }
    }


    @Override
    public void exitTelemetryFragment() {
        TelemetryFragment TelemetryFragment = (TelemetryFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (TelemetryFragment != null) {
            fragmentManager.beginTransaction().remove(TelemetryFragment).commitAllowingStateLoss();
        }
    }

    /**
     * Method to change sound
     * @param selection
     * @author Michael Reupold
     * edited by Aleksandra Targowicki (german to english, prevent confusion with useSound)
     */
    @Override
    public void setSound(int selection) {
        iSound = selection;
    }

    /**
     * author  Uwe
     */
    @Override
    public void exitVideoFragment() {
        VideoFragment videoFragment = (VideoFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (videoFragment != null) {
            fragmentManager.beginTransaction().remove(videoFragment).commitAllowingStateLoss();
        }
    }
}
