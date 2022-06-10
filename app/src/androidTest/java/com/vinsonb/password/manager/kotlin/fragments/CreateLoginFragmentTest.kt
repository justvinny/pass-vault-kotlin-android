package com.vinsonb.password.manager.kotlin.fragments

import android.content.Context
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.textfield.TextInputLayout
import com.vinsonb.password.manager.kotlin.R
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateLoginFragmentTest {
    private lateinit var scenario: FragmentScenario<CreateLoginFragment>
    private lateinit var targetContext: Context

    @Before
    fun setup() {
        scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_PasswordManagerKotlinRewrite)
        scenario.moveToState(Lifecycle.State.STARTED)
        targetContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @After()
    fun teardown() {
        scenario.moveToState(Lifecycle.State.DESTROYED)
        scenario.close()
    }

    @Test
    fun passcode_textInput_given5DigitsEntered_showsNoError() {
        onView(withId(R.id.passcode_text))
            .perform(typeText("55555"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.passcode))
            .check(matches(not(hasTextInputLayoutHintText(targetContext.resources.getString(R.string.error_passcode_length)))))
    }

    @Test
    fun passcode2_textInput_givenMatchingPasscode_showsNoError() {
        onView(withId(R.id.passcode_text))
            .perform(typeText("55555"))

        onView(withId(R.id.passcode_2_text))
            .perform(typeText("55555"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.passcode_2))
            .check(matches(not(hasTextInputLayoutHintText(targetContext.resources.getString(R.string.error_passcode_must_match)))))
    }

    @Test
    fun secretQuestion_textInput_givenText_showsNoError() {
        onView(withId(R.id.secret_question_text))
            .perform(typeText("Question"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.secret_question))
            .check(matches(not(hasTextInputLayoutHintText(targetContext.resources.getString(R.string.error_text_empty)))))
    }

    @Test
    fun secretAnswer_textInput_givenText_showsNoError() {
        onView(withId(R.id.secret_answer_text))
            .perform(typeText("Answer"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.secret_answer))
            .check(matches(not(hasTextInputLayoutHintText(targetContext.resources.getString(R.string.error_text_empty)))))
    }

    @Test
    fun buttonCreateLogin_clickEvent_givenValidData_navigatesToLoginFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        scenario.onFragment {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.create_login_fragment)
            Navigation.setViewNavController(it.requireView(), navController)
        }

        onView(withId(R.id.passcode_text))
            .perform(typeText("55555"))

        onView(withId(R.id.passcode_2_text))
            .perform(typeText("55555"))

        onView(withId(R.id.secret_question_text))
            .perform(typeText("Question"))

        onView(withId(R.id.secret_answer_text))
            .perform(typeText("Answer"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.button_create_login))
            .perform(click())

        Assert.assertEquals(navController.currentDestination?.id, R.id.login_fragment)
    }

    private fun hasTextInputLayoutHintText(expectedErrorText: String): Matcher<View> =
        object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {}

            override fun matchesSafely(item: View?): Boolean {
                if (item !is TextInputLayout) return false
                val error = item.hint ?: return false
                val hint = error.toString()
                return expectedErrorText == hint
            }
        }
}