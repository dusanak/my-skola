package com.skillsfighters.activity_tracker_android

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.skillsfighters.activity_tracker_android.activities.AuthenticationActivity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AuthenticationActivityTests {

    @get:Rule
    val intentsTestRule = IntentsTestRule(AuthenticationActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.skillsfighters.activity_tracker_android", appContext.packageName)
    }

    @Test
    fun continueButtonLaunchesGroupActivity() {
        onView(withId(R.id.continue_button))
            .perform(click())

        intended(toPackage("com.skillsfighters.activity_tracker_android"))
    }
}
