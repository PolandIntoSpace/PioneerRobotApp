package com.example.mischa.tasten_neigung;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.SeekBar;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.action.ViewActions.longClick;

/**
 * @author  Michael Reupold
 * Test class for Main Activity
 */
@RunWith(AndroidJUnit4.class)

public class Main_activity_Gui_Test_espresso {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    UDP_SERVER_TEST udp_server = new UDP_SERVER_TEST();

    @Test
    public void arrowUpTest() throws InterruptedException, IOException {
        udp_server.receiveData();
        for (int i = 0; i < 10; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.arrowUp)).perform(longClick());
        }
        byte[] byteMessage = udp_server.getMessage();
        String  message = String.format("%8s", Integer.toBinaryString(byteMessage[0] & 0xFF)).replace(' ', '0');
        if (!message.equals("00000001")) {
                throw new AssertionFailedError("arrowUpTest: \n expected: 00000001 \n given:    " + message);
        }
    }

    @Test
    public void arrowRightTest() throws IOException, InterruptedException {
        udp_server.receiveData();
        for (int i = 0; i < 10; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.arrowRight)).perform(longClick());
        }
        byte[] byteMessage = udp_server.getMessage();
        String  message = String.format("%8s", Integer.toBinaryString(byteMessage[0] & 0xFF)).replace(' ', '0');
        if (!message.equals("10001000")) {
            throw new AssertionFailedError("arrowRightTest: \n expected: 10001000 \n given:    " + message);
        }
    }

    @Test
    public void arrowDownTest() throws IOException, InterruptedException {
        udp_server.receiveData();
        for (int i = 0; i < 10; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.arrowDown)).perform(longClick());
        }
        byte[] byteMessage = udp_server.getMessage();
        String  message = String.format("%8s", Integer.toBinaryString(byteMessage[0] & 0xFF)).replace(' ', '0');
        if (!message.equals("01000001")) {
            throw new AssertionFailedError("arrowDownTest: \n expected: 01000001 \n given:    " + message);
        }
    }

    @Test
    public void arrowLeftTest() throws IOException, InterruptedException {
        udp_server.receiveData();
        for (int i = 0; i < 10; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.arrowLeft)).perform(longClick());
        }
        byte[] byteMessage = udp_server.getMessage();
        String  message = String.format("%8s", Integer.toBinaryString(byteMessage[0] & 0xFF)).replace(' ', '0');
        if (!message.equals("00001000")) {
            throw new AssertionFailedError("arrowLeftTest: \n expected: 00001000 \n given:    " + message);
        }
    }

    @Test
    public void intensityTwoTest() throws IOException, InterruptedException {
        udp_server.receiveData();
        Espresso.onView(ViewMatchers.withId(R.id.intensitySlider)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Set progress on seek bar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(1);
            }
        });
        for (int i = 0; i < 10; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.arrowLeft)).perform(longClick());
        }
        byte[] byteMessage = udp_server.getMessage();
        String  message = String.format("%8s", Integer.toBinaryString(byteMessage[0] & 0xFF)).replace(' ', '0');
        if (!message.equals("00010000")) {
            throw new AssertionFailedError("intensityTwoTest: \n expected: 00010000 \n given:    " + message);
        }
    }

    @Test
    public void intensityThreeTest() throws IOException, InterruptedException {
        udp_server.receiveData();
        Espresso.onView(ViewMatchers.withId(R.id.intensitySlider)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Set progress on seek bar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(2);
            }
        });
        for (int i = 0; i < 10; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.arrowLeft)).perform(longClick());
        }
        byte[] byteMessage = udp_server.getMessage();
        String  message = String.format("%8s", Integer.toBinaryString(byteMessage[0] & 0xFF)).replace(' ', '0');
        if (!message.equals("00011000")) {
            throw new AssertionFailedError("intensityThreeTest: \n expected: 00011000 \n given:    " + message);
        }
    }

    @Test
    public void intensityFourTest() throws IOException, InterruptedException {
        udp_server.receiveData();
        Espresso.onView(ViewMatchers.withId(R.id.intensitySlider)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Set progress on seek bar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(3);
            }
        });
        for (int i = 0; i < 10; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.arrowLeft)).perform(longClick());
        }
        byte[] byteMessage = udp_server.getMessage();
        String  message = String.format("%8s", Integer.toBinaryString(byteMessage[0] & 0xFF)).replace(' ', '0');
        if (!message.equals("00100000")) {
            throw new AssertionFailedError("intensityFourTest: \n expected: 00100000 \n given:    " + message);
        }
    }

    @Test
    public void intensityFiveTest() throws IOException, InterruptedException {
        Espresso.onView(ViewMatchers.withId(R.id.intensitySlider)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Set progress on seek bar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(4);
            }
        });

        udp_server.receiveData();
        for (int i = 0; i < 10; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.arrowLeft)).perform(longClick());
        }

        byte[] byteMessage = udp_server.getMessage();
        String  message = String.format("%8s", Integer.toBinaryString(byteMessage[0] & 0xFF)).replace(' ', '0');
        if (!message.equals("00101000")) {
            throw new AssertionFailedError("intensityFiveTest: \n expected: 00101000 \n given:    " + message);
        }
    }
}

