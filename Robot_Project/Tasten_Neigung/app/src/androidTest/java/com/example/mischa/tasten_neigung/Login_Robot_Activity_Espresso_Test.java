package com.example.mischa.tasten_neigung;

import android.content.ComponentName;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * @author Michael Reupold
 * edited by Aleksandra Targowicki (changed hard coded strings)
 * Test Class for Login
 */

// READ ME
// Deaktiviere:
//                   1. Skalierung Fensteranimation
//                   2. Skalierung Übergansanimation
//                   3. Skalierung Animationsdauer
// in den Entwickleroptionen
// Kommentar: Zusatz in Gradle
// androidTestCompile('com.android.support.test.espresso:espresso-intents:2.2.1', {
//                    exclude group: 'com.android.support', module: 'support-annotations'
//                   })

@RunWith(AndroidJUnit4.class)

public class Login_Robot_Activity_Espresso_Test {

    @Rule
    public IntentsTestRule<LoginRobotActivity> mActivityRule = new IntentsTestRule<LoginRobotActivity>(LoginRobotActivity.class);

    @Test
    public void too_long_IP() {
        Espresso.onView(ViewMatchers.withId(R.id.editText_login)).perform(replaceText("1.2.3.4.5"));
        Espresso.onView(ViewMatchers.withId(R.id.button_login)).perform(click());
        Espresso.onView(withText(R.string.loginErrorIP)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void too_short_IP() {
        Espresso.onView(ViewMatchers.withId(R.id.editText_login)).perform(replaceText("1.2.3"));
        Espresso.onView(ViewMatchers.withId(R.id.button_login)).perform(click());
        Espresso.onView(withText(R.string.loginErrorIP)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void address_number_too_big_IP() {
        Espresso.onView(ViewMatchers.withId(R.id.editText_login)).perform(replaceText("1.2.3.300"));
        Espresso.onView(ViewMatchers.withId(R.id.button_login)).perform(click());
        Espresso.onView(withText(R.string.loginErrorIP)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void address_number_too_small_IP() {
        Espresso.onView(ViewMatchers.withId(R.id.editText_login)).perform(replaceText("1.2.3.-2"));
        Espresso.onView(ViewMatchers.withId(R.id.button_login)).perform(click());
        Espresso.onView(withText(R.string.loginErrorIP)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void valid_IP() {
        Espresso.onView(ViewMatchers.withId(R.id.editText_login)).perform(replaceText("1.2.3.4"));
        Espresso.onView(ViewMatchers.withId(R.id.button_login)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), MainActivity.class)));
    }
}
