package com.vinsonb.password.manager.kotlin.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.PASSCODE_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {
    private lateinit var scenario: FragmentScenario<LoginFragment>

    private val targetContext: Context = ApplicationProvider.getApplicationContext()
    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(targetContext)

    @Before
    fun setup() {
        with(preferences.edit()) {
            putString(PASSCODE_KEY, "11111")
            putString(SECRET_ANSWER_KEY, "Answer")
            apply()
        }

        scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_PasswordManagerKotlinRewrite)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @After()
    fun teardown() {
        with(preferences.edit()) {
            putBoolean(AUTHENTICATED_KEY, false)
            apply()
        }

        scenario.moveToState(Lifecycle.State.DESTROYED)
        scenario.close()
    }

    @Test
    fun validPassword_entered_navigateToHomeFragment() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        scenario.onFragment {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.login_fragment)
            Navigation.setViewNavController(it.requireView(), navController)
        }

        repeat(5) {
            onView(withId(R.id.button_1))
                .perform(click())
        }

        Assert.assertEquals(navController.currentDestination?.id, R.id.view_accounts_fragment)
    }

    @Test
    fun forgotPasswordButton_clicked_showsDialogVerifySecret() {
        onView(withId(R.id.button_forgot_password))
            .perform(click())

        onView(withId(R.id.layout_dialog_verify_secret_answer))
            .check(matches(isDisplayed()))
    }
}