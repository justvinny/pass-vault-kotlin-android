package com.vinsonb.password.manager.kotlin.activities


import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    private val targetContext: Context = ApplicationProvider.getApplicationContext()
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(targetContext)

    @Before
    fun setup() {
        with(sharedPreferences.edit()) {
            putBoolean(AUTHENTICATED_KEY, true)
            apply()
        }
    }

    @Test
    fun bottomNavigationBar_displaysCorrectMenuItems() {
        lateinit var bottomNavigationMenu: Menu
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            val bottomNavigationView =
                it.findViewById(R.id.bottom_navigation) as BottomNavigationView
            bottomNavigationMenu = bottomNavigationView.menu
        }

        for (i in 0 until bottomNavigationMenu.size()) {
            val menuItem = bottomNavigationMenu.getItem(i)
            assertEquals(MENU_ITEM_IDS[i], menuItem.itemId)
        }

        scenario.close()
    }

    @Test
    fun menuItemLogout_clicked_navigatesToLoginFragment() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            val menuItem = it.findViewById<MaterialToolbar>(R.id.top_navigation)
                .menu.findItem(R.id.menu_item_logout)
            // Remove Logout from Overflow Menu and show it on the app bar for this test.
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }

        onView(withText(targetContext.getString(R.string.menu_item_logout)))
            .perform(click())

        // 2 cases since the CI test will go to the CreateLoginFragment as there is no existing passcode
        val expectedDestination1 = NavDestination.getDisplayName(targetContext, R.id.login_fragment)
        val expectedDestination2 =
            NavDestination.getDisplayName(targetContext, R.id.create_login_fragment)
        lateinit var navController: NavController
        scenario.onActivity {
            navController = it.findNavController(R.id.nav_host_fragment)
        }
        val actualDestination = navController.currentDestination?.displayName

        assertThat(actualDestination, anyOf(`is`(expectedDestination1), `is`(expectedDestination2)))
    }

    companion object {
        private val MENU_ITEM_IDS = listOf(
            R.id.view_accounts_fragment,
            R.id.save_account_fragment,
            R.id.generate_password_fragment
        )
    }
}