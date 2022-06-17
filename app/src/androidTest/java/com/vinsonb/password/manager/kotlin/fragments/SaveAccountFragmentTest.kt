package com.vinsonb.password.manager.kotlin.fragments

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.vinsonb.password.manager.kotlin.HiltTestActivity
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.di.launchFragmentInHiltContainer
import com.vinsonb.password.manager.kotlin.matchers.TextInputLayoutMatchers.Companion.containsTextInputLayoutErrorText
import com.vinsonb.password.manager.kotlin.matchers.TextInputLayoutMatchers.Companion.withTextInputLayoutErrorText
import com.vinsonb.password.manager.kotlin.matchers.TextInputLayoutMatchers.Companion.withTextInputLayoutIsErrorEnabled
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SaveAccountFragmentTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule: ActivityScenarioRule<HiltTestActivity> =
        ActivityScenarioRule(HiltTestActivity::class.java)

    private var context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        hiltRule.inject()
        launchFragmentInHiltContainer<SaveAccountFragment>()
    }

    @Test
    fun allTextInput_haveErrorEnabledByDefault() {
        val expectedIsErrorEnabled = true

        onView(withId(R.id.layout_platform)).check(
            matches(
                withTextInputLayoutIsErrorEnabled(
                    expectedIsErrorEnabled
                )
            )
        )

        onView(withId(R.id.layout_username)).check(
            matches(
                withTextInputLayoutIsErrorEnabled(
                    expectedIsErrorEnabled
                )
            )
        )

        onView(withId(R.id.layout_password)).check(
            matches(
                withTextInputLayoutIsErrorEnabled(
                    expectedIsErrorEnabled
                )
            )
        )

        onView(withId(R.id.layout_repeat_password)).check(
            matches(
                withTextInputLayoutIsErrorEnabled(
                    expectedIsErrorEnabled
                )
            )
        )
    }

    @Test
    fun allTextInput_givenBlankInput_haveAppropriateErrorMessage() {
        val expectedPlatformErrorMsg = "not be empty"

        onView(withId(R.id.layout_platform)).check(
            matches(
                containsTextInputLayoutErrorText(
                    expectedPlatformErrorMsg
                )
            )
        )

        onView(withId(R.id.layout_username)).check(
            matches(
                containsTextInputLayoutErrorText(
                    expectedPlatformErrorMsg
                )
            )
        )

        onView(withId(R.id.layout_password)).check(
            matches(
                containsTextInputLayoutErrorText(
                    expectedPlatformErrorMsg
                )
            )
        )

        onView(withId(R.id.layout_repeat_password)).check(
            matches(
                containsTextInputLayoutErrorText(
                    expectedPlatformErrorMsg
                )
            )
        )
    }

    @Test
    fun textInputPlatform_givenString_hasErrorDisabled() {
        val expectedIsErrorEnabled = false
        val dummyPlatform = "Amazon"

        onView(withId(R.id.input_platform))
            .perform(typeText(dummyPlatform), closeSoftKeyboard())

        onView(withId(R.id.layout_platform))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))
    }

    @Test
    fun textInputUsername_givenString_hasErrorDisabled() {
        val expectedIsErrorEnabled = false
        val dummyUsername = "john.doe"

        onView(withId(R.id.input_username))
            .perform(typeText(dummyUsername), closeSoftKeyboard())

        onView(withId(R.id.layout_username))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))
    }

    @Test
    fun textInputPassword_givenString_hasErrorDisabled() {
        val expectedIsErrorEnabled = false
        val dummyPassword = "test.password"

        onView(withId(R.id.input_password))
            .perform(typeText(dummyPassword), closeSoftKeyboard())

        onView(withId(R.id.layout_password))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))
    }

    @Test
    fun textInputRepeatPassword_givenStringAndMatchingPassword_hasErrorDisabled() {
        val expectedIsErrorEnabled = false
        val dummyPassword = "test.password"

        onView(withId(R.id.input_password))
            .perform(typeText(dummyPassword), closeSoftKeyboard())

        onView(withId(R.id.input_repeat_password))
            .perform(typeText(dummyPassword), closeSoftKeyboard())

        onView(withId(R.id.layout_password))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))

        onView(withId(R.id.layout_repeat_password))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))
    }

    @Test
    fun textInputRepeatPassword_givenNotMatchingPassword_hasErrorEnabledAndShowsAppropriateMessage() {
        val expectedIsErrorEnabled = true
        val expectedErrorMessage = context.resources.getString(R.string.error_password_must_match)
        val dummyPassword = "test.password"
        val dummyRepeatPassword = "test_password"

        onView(withId(R.id.input_password))
            .perform(typeText(dummyPassword), closeSoftKeyboard())

        onView(withId(R.id.input_repeat_password))
            .perform(typeText(dummyRepeatPassword), closeSoftKeyboard())

        onView(withId(R.id.layout_repeat_password))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))

        onView(withId(R.id.layout_repeat_password))
            .check(matches(withTextInputLayoutErrorText(expectedErrorMessage)))
    }

    @Test
    fun buttonSaveAccount_givenValidDataFromForm_clearsAllFields() {
        val fakePlatform = "Amazon"
        val fakeUsername = "john.doe"
        val fakePassword = "good.password"

        onView(withId(R.id.input_platform))
            .perform(typeText(fakePlatform), closeSoftKeyboard())

        onView(withId(R.id.input_username))
            .perform(typeText(fakeUsername), closeSoftKeyboard())

        onView(withId(R.id.input_password))
            .perform(typeText(fakePassword), closeSoftKeyboard())

        onView(withId(R.id.input_repeat_password))
            .perform(typeText(fakePassword), closeSoftKeyboard())

        onView(withId(R.id.button_save_account))
            .perform(click())

        onView(withId(R.id.input_platform))
            .check(matches(withText("")))

        onView(withId(R.id.input_username))
            .check(matches(withText("")))

        onView(withId(R.id.input_password))
            .check(matches(withText("")))

        onView(withId(R.id.input_repeat_password))
            .check(matches(withText("")))
    }
}