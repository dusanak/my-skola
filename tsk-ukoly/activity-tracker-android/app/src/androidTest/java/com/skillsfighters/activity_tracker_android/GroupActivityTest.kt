package com.skillsfighters.activity_tracker_android

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.skillsfighters.activity_tracker_android.activities.GroupActivity
import kotlinx.android.synthetic.main.activity_group.*
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.endsWith
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime


@RunWith(AndroidJUnit4::class)
class GroupActivityTest {

    private val testGroupName = LocalDateTime.now().hashCode().toString()

    @get:Rule
    val activityTestRule = ActivityTestRule(GroupActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.skillsfighters.activity_tracker_android", appContext.packageName)
    }

    @Test
    fun addGroup() {
        onView(withId(R.id.refresh))
            .perform(click())

        Thread.sleep(5000)

        val numberOfItems = activityTestRule.activity.rv_item_list.adapter!!.itemCount

        onView(withId(R.id.fab_add))
            .perform(click())

        onView(withText(R.string.new_group))
            .check(matches(isDisplayed()))

        onView(withClassName(endsWith("EditText")))
            .perform(replaceText(testGroupName))

        onView(withText(R.string.save))
            .perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.refresh))
            .perform(click())

        Thread.sleep(5000)

        assertEquals(numberOfItems + 1, activityTestRule.activity.rv_item_list.adapter!!.itemCount)
    }

    @Test
    fun showGroup() {
        onView(withId(R.id.fab_add))
            .perform(click())

        onView(withText(R.string.new_group))
            .check(matches(isDisplayed()))

        onView(withClassName(endsWith("EditText")))
            .perform(replaceText(testGroupName))

        onView(withText(R.string.save))
            .perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.refresh))
            .perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.group_name))
            .check(matches(withText(R.string.root_group)))

        onView(withId(R.id.rv_item_list))
            .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText(testGroupName)), click()))

        Thread.sleep(5000)

        onView(withId(R.id.group_name))
            .check(matches(withText(containsString(testGroupName))))
    }
}