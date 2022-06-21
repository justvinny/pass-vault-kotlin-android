package com.vinsonb.password.manager.kotlin.fragments

import android.content.Context
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.di.FakeData
import com.vinsonb.password.manager.kotlin.di.FakeDatabaseModule
import com.vinsonb.password.manager.kotlin.di.launchFragmentInHiltContainer
import com.vinsonb.password.manager.kotlin.matchers.RecyclerViewMatchers
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        with(preferences.edit()) {
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
        with(preferences.edit()) {
            clear()
            apply()
        }
    }

    @Test
    fun recyclerView_populatedWithFakeData_displaysAllFakeData() {
        CoroutineScope(IO).launch {
            FakeDatabaseModule.populateFakeData()

            withContext(Main) {
                FakeData.FAKE_ACCOUNTS.forEachIndexed { index, account ->
                    Espresso.onView(ViewMatchers.withId(R.id.recycler_view_accounts))
                        .check(
                            ViewAssertions.matches(
                                RecyclerViewMatchers.withPositionMatchesAccount(
                                    index,
                                    ViewMatchers.hasDescendant(ViewMatchers.withText(account.platform))
                                )
                            )
                        )
                        .check(
                            ViewAssertions.matches(
                                RecyclerViewMatchers.withPositionMatchesAccount(
                                    index,
                                    ViewMatchers.hasDescendant(ViewMatchers.withText(account.username))
                                )
                            )
                        )
                }
            }
        }
    }
}