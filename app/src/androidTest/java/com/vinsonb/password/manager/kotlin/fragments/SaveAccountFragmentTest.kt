package com.vinsonb.password.manager.kotlin.fragments

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.matchers.TextInputLayoutMatchers.Companion.containsTextInputLayoutErrorText
import com.vinsonb.password.manager.kotlin.matchers.TextInputLayoutMatchers.Companion.withTextInputLayoutErrorText
import com.vinsonb.password.manager.kotlin.matchers.TextInputLayoutMatchers.Companion.withTextInputLayoutIsErrorEnabled
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveAccountFragmentTest {
    private lateinit var scenario: FragmentScenario<SaveAccountFragment>
    private lateinit var targetContext: Context

    @Before
    fun setup() {
        scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_PasswordManagerKotlinRewrite)
        scenario.moveToState(Lifecycle.State.STARTED)
        targetContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @After
    fun teardown() {
        scenario.moveToState(Lifecycle.State.DESTROYED)
        scenario.close()
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
            .perform(typeText(dummyPlatform))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.layout_platform))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))
    }

    @Test
    fun textInputUsername_givenString_hasErrorDisabled() {
        val expectedIsErrorEnabled = false
        val dummyUsername = "john.doe"

        onView(withId(R.id.input_username))
            .perform(typeText(dummyUsername))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.layout_username))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))
    }

    @Test
    fun textInputPassword_givenString_hasErrorDisabled() {
        val expectedIsErrorEnabled = false
        val dummyPassword = "test.password"

        onView(withId(R.id.input_password))
            .perform(typeText(dummyPassword))
            .perform(closeSoftKeyboard())

        onView(withId(R.id.layout_password))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))
    }

    @Test
    fun textInputRepeatPassword_givenStringAndMatchingPassword_hasErrorDisabled() {
        val expectedIsErrorEnabled = false
        val dummyPassword = "test.password"

        onView(withId(R.id.input_password))
            .perform(typeText(dummyPassword))

        onView(withId(R.id.input_repeat_password))
            .perform(typeText(dummyPassword))

        closeSoftKeyboard()

        onView(withId(R.id.layout_password))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))

        onView(withId(R.id.layout_repeat_password))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))
    }

    @Test
    fun textInputRepeatPassword_givenNotMatchingPassword_hasErrorEnabledAndShowsAppropriateMessage() {
        val expectedIsErrorEnabled = true
        val expectedErrorMessage =
            targetContext.resources.getString(R.string.error_password_must_match)
        val dummyPassword = "test.password"
        val dummyRepeatPassword = "test_password"

        onView(withId(R.id.input_password))
            .perform(typeText(dummyPassword))

        onView(withId(R.id.input_repeat_password))
            .perform(typeText(dummyRepeatPassword))

        closeSoftKeyboard()

        onView(withId(R.id.layout_repeat_password))
            .check(matches(withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled)))

        onView(withId(R.id.layout_repeat_password))
            .check(matches(withTextInputLayoutErrorText(expectedErrorMessage)))
    }
}