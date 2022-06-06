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
import androidx.test.platform.app.InstrumentationRegistry
import com.vinsonb.password.manager.kotlin.Constants
import com.vinsonb.password.manager.kotlin.R
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LoginFragmentTest {
    private lateinit var scenario: FragmentScenario<LoginFragment>
    private lateinit var targetContext: Context
    private lateinit var preferences: SharedPreferences

    @Before
    fun setup() {
        scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_PasswordManagerKotlinRewrite)
        scenario.moveToState(Lifecycle.State.STARTED)
        targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        preferences = PreferenceManager.getDefaultSharedPreferences(targetContext)
    }

    @After()
    fun teardown() {
        scenario.moveToState(Lifecycle.State.DESTROYED)
        scenario.close()
    }

    @Test
    fun validPassword_entered_navigateToHomeFragment() {
        with(preferences.edit()) {
            putString(Constants.Companion.Password.SharedPreferenceKeys.PASSCODE_KEY, "55555")
            apply()
        }

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        scenario.onFragment {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.loginFragment)
            Navigation.setViewNavController(it.requireView(), navController)
        }

        repeat(5) {
            Espresso.onView(ViewMatchers.withId(R.id.button_5))
                .perform(ViewActions.click())
        }

        Assert.assertEquals(navController.currentDestination?.id, R.id.viewAccountsFragment)

        with(preferences.edit()) {
            clear()
            apply()
        }
    }
}