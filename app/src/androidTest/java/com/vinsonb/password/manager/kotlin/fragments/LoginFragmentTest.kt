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
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.utilities.Constants
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LoginFragmentTest {
    private lateinit var scenario: FragmentScenario<LoginFragment>

    private val targetContext: Context = ApplicationProvider.getApplicationContext()
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(targetContext)

    @Before
    fun setup() {
        with(preferences.edit()) {
            putString(Constants.Password.SharedPreferenceKeys.PASSCODE_KEY, "55555")
            apply()
        }

        scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_PasswordManagerKotlinRewrite)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @After()
    fun teardown() {
        with(preferences.edit()) {
            clear()
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
            Espresso.onView(ViewMatchers.withId(R.id.button_5))
                .perform(ViewActions.click())
        }

        Assert.assertEquals(navController.currentDestination?.id, R.id.view_accounts_fragment)
    }
}