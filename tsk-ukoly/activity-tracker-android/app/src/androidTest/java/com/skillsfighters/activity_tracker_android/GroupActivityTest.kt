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
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(Enclosed::class)
class GroupActivityTest {

    @RunWith(value = Parameterized::class)
    class GroupActivityCreateTest(
        private val testGroupName: String,
        private val expected: Boolean
    ) {

        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "{index}: isValid({0})={1}")
            fun data(): Iterable<Array<Any>> {
                return arrayListOf(
                    arrayOf("test", true),
                    arrayOf("český test", true),
                    arrayOf("123", false),
                    arrayOf("-123", false),
                    arrayOf("test 123", true),
                    arrayOf("", false),
                    arrayOf(" ", false),
                    arrayOf("   ", false),
                    arrayOf("測試", true)
                )
            }
        }

        @get:Rule
        val activityTestRule = ActivityTestRule(GroupActivity::class.java)

        @Test
        fun addGroup() {
            onView(withId(R.id.refresh))
                .perform(click())

            Thread.sleep(1000)

            val numberOfItems = activityTestRule.activity.rv_item_list.adapter!!.itemCount

            onView(withId(R.id.fab_add))
                .perform(click())

            onView(withText(R.string.new_group))
                .check(matches(isDisplayed()))

            onView(withClassName(endsWith("EditText")))
                .perform(replaceText(testGroupName))

            onView(withText(R.string.save))
                .perform(click())

            Thread.sleep(1000)

            onView(withId(R.id.refresh))
                .perform(click())

            Thread.sleep(1000)

            if (expected) {
                assertEquals(
                    numberOfItems + 1,
                    activityTestRule.activity.rv_item_list.adapter!!.itemCount
                )
            }
            else {
                assertEquals(
                    numberOfItems,
                    activityTestRule.activity.rv_item_list.adapter!!.itemCount
                )
            }
        }

        @After
        fun deleteAll() {
            while (activityTestRule.activity.rv_item_list.adapter!!.itemCount > 0) {
                onView(first(withId(R.id.delete_button)))
                    .perform(
                        click()
                    )

                Thread.sleep(1000)

                onView(withText(R.string.delete))
                    .perform(click())

                Thread.sleep(1000)
            }
        }
    }

    @RunWith(AndroidJUnit4::class)
    class GroupActivityShowTest {
        @get:Rule
        val activityTestRule = ActivityTestRule(GroupActivity::class.java)

        @Test
        fun useAppContext() {
            // Context of the app under test.
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            assertEquals("com.skillsfighters.activity_tracker_android", appContext.packageName)
        }

        @Test
        fun showGroup() {
            val testGroupName = "ShowTest"

            onView(withId(R.id.fab_add))
                .perform(click())

            onView(withText(R.string.new_group))
                .check(matches(isDisplayed()))

            onView(withClassName(endsWith("EditText")))
                .perform(replaceText(testGroupName))

            onView(withText(R.string.save))
                .perform(click())

            Thread.sleep(1000)

            onView(withId(R.id.refresh))
                .perform(click())

            Thread.sleep(1000)

            onView(withId(R.id.group_name))
                .check(matches(withText(R.string.root_group)))

            onView(withId(R.id.rv_item_list))
                .perform(
                    RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                        hasDescendant(withText(testGroupName)), click()
                    )
                )

            Thread.sleep(1000)

            onView(withId(R.id.group_name))
                .check(matches(withText(containsString(testGroupName))))
        }

        @Test
        fun deleteFirst() {
            val testGroupName = "DeleteTest"

            onView(withId(R.id.fab_add))
                .perform(click())

            onView(withText(R.string.new_group))
                .check(matches(isDisplayed()))

            onView(withClassName(endsWith("EditText")))
                .perform(replaceText(testGroupName))

            onView(withText(R.string.save))
                .perform(click())

            Thread.sleep(1000)


            val numberOfItems = activityTestRule.activity.rv_item_list.adapter!!.itemCount

            onView(first(withId(R.id.delete_button)))
                .perform(
                    click()
                )

            Thread.sleep(1000)

            onView(withText(R.string.delete))
                .perform(click())

            Thread.sleep(1000)

            if (numberOfItems > 0) {
                assertEquals(
                    numberOfItems - 1,
                    activityTestRule.activity.rv_item_list.adapter!!.itemCount
                )
            }
            else {
                assertEquals(
                    0,
                    activityTestRule.activity.rv_item_list.adapter!!.itemCount
                )
            }
        }

        @Test
        fun deleteFirstCancel() {
            val testGroupName = "DeleteTest"

            onView(withId(R.id.fab_add))
                .perform(click())

            onView(withText(R.string.new_group))
                .check(matches(isDisplayed()))

            onView(withClassName(endsWith("EditText")))
                .perform(replaceText(testGroupName))

            onView(withText(R.string.save))
                .perform(click())

            Thread.sleep(1000)


            val numberOfItems = activityTestRule.activity.rv_item_list.adapter!!.itemCount

            onView(first(withId(R.id.delete_button)))
                .perform(
                    click()
                )

            Thread.sleep(1000)

            onView(withText(R.string.cancel))
                .perform(click())

            Thread.sleep(1000)

            assertEquals(
                numberOfItems,
                activityTestRule.activity.rv_item_list.adapter!!.itemCount
            )
        }

        @After
        fun deleteAll() {
            while (activityTestRule.activity.rv_item_list.adapter!!.itemCount > 0) {
                onView(first(withId(R.id.delete_button)))
                    .perform(
                        click()
                    )

                Thread.sleep(2000)

                onView(withText(R.string.delete))
                    .perform(click())
            }
        }
    }
}

private fun <T> first(matcher: Matcher<T>): Matcher<T>? {
    return object : BaseMatcher<T>() {
        var isFirst = true
        override fun matches(item: Any): Boolean {
            if (isFirst && matcher.matches(item)) {
                isFirst = false
                return true
            }
            return false
        }

        override fun describeTo(description: Description) {
            description.appendText("should return first matching item")
        }
    }
}