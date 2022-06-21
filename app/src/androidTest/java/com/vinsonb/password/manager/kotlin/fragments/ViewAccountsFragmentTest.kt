package com.vinsonb.password.manager.kotlin.fragments

import android.content.Context
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.di.launchFragmentInHiltContainer
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ViewAccountsFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private var targetContext: Context = ApplicationProvider.getApplicationContext()
    private var preferences = PreferenceManager.getDefaultSharedPreferences(targetContext)

    @Before
    fun setup() {
        hiltRule.inject()

        with (preferences.edit()) {
            putBoolean(AUTHENTICATED_KEY, true)
            apply()
        }

        val navController = TestNavHostController(targetContext)
        launchFragmentInHiltContainer<ViewAccountsFragment> {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.view_accounts_fragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @After
    fun teardown() {
        with (preferences.edit()) {
            clear()
            apply()
        }
    }

    @Test
    fun test() {
        assert(true)
        Thread.sleep(1000L)
    }
}