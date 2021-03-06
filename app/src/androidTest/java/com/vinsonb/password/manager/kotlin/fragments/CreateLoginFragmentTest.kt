package com.vinsonb.password.manager.kotlin.fragments

import android.content.Context
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
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.utilities.TextInputLayoutMatchers.withTextInputLayoutErrorText
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateLoginFragmentTest {
    private lateinit var scenario: FragmentScenario<CreateLoginFragment>

    private val targetContext: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_PasswordManagerKotlinRewrite)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @After()
    fun teardown() {
        scenario.moveToState(Lifecycle.State.DESTROYED)
        scenario.close()
    }

    @Test
    fun inputPasscode_given5DigitsEntered_showsNoError() {
        onView(withId(R.id.input_passcode))
            .perform(typeText("55555"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.layout_passcode))
            .check(matches(not(withTextInputLayoutErrorText(targetContext.resources.getString(R.string.error_passcode_length)))))
    }

    @Test
    fun inputRepeatPasscode_givenMatchingPasscode_showsNoError() {
        onView(withId(R.id.input_passcode))
            .perform(typeText("55555"))

        onView(withId(R.id.input_repeat_passcode))
            .perform(typeText("55555"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.layout_repeat_passcode))
            .check(matches(not(withTextInputLayoutErrorText(targetContext.resources.getString(R.string.error_passcode_must_match)))))
    }

    @Test
    fun inputSecretQuestion_givenText_showsNoError() {
        onView(withId(R.id.input_secret_question))
            .perform(typeText("Question"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.layout_secret_question))
            .check(
                matches(
                    not(
                        withTextInputLayoutErrorText(
                            targetContext.resources.getString(
                                R.string.error_text_empty,
                                R.string.hint_secret_question
                            )
                        )
                    )
                )
            )
    }

    @Test
    fun inputSecretAnswer_givenText_showsNoError() {
        onView(withId(R.id.input_secret_answer))
            .perform(typeText("Answer"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.layout_secret_answer))
            .check(
                matches(
                    not(
                        withTextInputLayoutErrorText(
                            targetContext.resources.getString(
                                R.string.error_text_empty,
                                R.string.hint_secret_answer
                            )
                        )
                    )
                )
            )
    }

    @Test
    fun buttonCreateLogin_clicked_givenValidData_navigatesToLoginFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        scenario.onFragment {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.create_login_fragment)
            Navigation.setViewNavController(it.requireView(), navController)
        }

        onView(withId(R.id.input_passcode))
            .perform(typeText("55555"))

        onView(withId(R.id.input_repeat_passcode))
            .perform(typeText("55555"))

        onView(withId(R.id.input_secret_question))
            .perform(typeText("Question"))

        onView(withId(R.id.input_secret_answer))
            .perform(typeText("Answer"))

        Espresso.closeSoftKeyboard()

        onView(withId(R.id.button_create_login))
            .perform(click())

        Assert.assertEquals(navController.currentDestination?.id, R.id.login_fragment)
    }
}