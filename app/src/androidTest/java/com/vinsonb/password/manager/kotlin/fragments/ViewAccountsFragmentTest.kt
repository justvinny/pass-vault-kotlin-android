package com.vinsonb.password.manager.kotlin.fragments

import android.content.ClipboardManager
import android.content.Context
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.di.FakeData
import com.vinsonb.password.manager.kotlin.di.FakeDatabaseModule
import com.vinsonb.password.manager.kotlin.di.launchFragmentInHiltContainer
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import com.vinsonb.password.manager.kotlin.utilities.RecyclerViewMatchers
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
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
    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        hiltRule.inject()

        with(preferences.edit()) {
            putBoolean(AUTHENTICATED_KEY, true)
            apply()
        }

        navController = TestNavHostController(targetContext)
        launchFragmentInHiltContainer<ViewAccountsFragment> {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.view_accounts_fragment)
            Navigation.setViewNavController(requireView(), navController)
        }
    }

    @After
    fun teardown() {
        with(preferences.edit()) {
            putBoolean(AUTHENTICATED_KEY, false)
            apply()
        }
    }

    @Test
    fun recyclerView_populatedWithFakeData_displaysAllFakeData() {
        runBlocking {
            FakeDatabaseModule.populateFakeData()

            FakeData.FAKE_ACCOUNTS.forEachIndexed { index, account ->
                onView(withId(R.id.recycler_view_accounts))
                    .check(
                        matches(
                            RecyclerViewMatchers.withPositionMatchesText(
                                index,
                                hasDescendant(withText(account.platform))
                            )
                        )
                    )
                    .check(
                        matches(
                            RecyclerViewMatchers.withPositionMatchesText(
                                index,
                                hasDescendant(withText(account.username))
                            )
                        )
                    )
            }
        }
    }

    @Test
    fun accountDialog_givenFakeAccount_displaysAndFunctionsProperly() {
        runBlocking {
            val platform = "TestPlatform"
            val username = "testing.user"
            val password = "testing.pass"
            val fakeAccount = Account(platform, username, password)
            FakeDatabaseModule.addFakeAccount(fakeAccount)

            // Open Account Dialog
            onView(withContentDescription(targetContext.resources.getString(R.string.content_maximize_account)))
                .perform(click())

            // Make password visible
            onView(withId(R.id.icon_accounts_dialog_see_password))
                .perform(click())

            // Validate data displayed correctly.
            onView(withId(R.id.text_accounts_dialog_platform))
                .check(matches(withText(platform)))

            onView(withId(R.id.text_accounts_dialog_username))
                .check(matches(withText(username)))

            onView(withId(R.id.input_accounts_dialog_password))
                .check(matches(allOf(withText(password), not(isEnabled()))))

            // Validate password input enabled
            onView(withId(R.id.icon_accounts_dialog_edit))
                .perform(click())

            onView(withId(R.id.input_accounts_dialog_password))
                .check(matches(isEnabled()))

            // Test copy and paste functionalities.
            val clipboardManager: ClipboardManager?
            withContext(Main) {
                clipboardManager = targetContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            }

            if (clipboardManager != null) {
                onView(withId(R.id.icon_accounts_dialog_copy_username))
                    .perform(click())

                onView(withId(R.id.text_accounts_dialog_username))
                    .check(matches(withText(clipboardManager.primaryClip?.getItemAt(0)?.text.toString())))

                onView(withId(R.id.icon_accounts_dialog_copy_password))
                    .perform(click())

                onView(withId(R.id.input_accounts_dialog_password))
                    .check(matches(withText(clipboardManager.primaryClip?.getItemAt(0)?.text.toString())))
            }

            // Test delete functionality
            onView(withId(R.id.icon_accounts_dialog_delete))
                .perform(click())

            onView(allOf(withText(platform), withText(username)))
                .check(doesNotExist())
        }
    }
}