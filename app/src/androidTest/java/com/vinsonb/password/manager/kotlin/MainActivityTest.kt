package com.vinsonb.password.manager.kotlin


import android.content.Context
import android.view.Menu
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vinsonb.password.manager.kotlin.activities.MainActivity
import com.vinsonb.password.manager.kotlin.utilities.Constants.Companion.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.*
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomNavigationMenu: Menu
    private lateinit var menuStringContent: HashMap<Int, String>

    @Before
    fun setup() {
        scenario = activityScenarioRule.scenario
        scenario.onActivity {
            val preferences = it.getPreferences(Context.MODE_PRIVATE)
            with(preferences.edit()) {
                putBoolean(AUTHENTICATED_KEY, true)
                apply()
            }

            bottomNavigationView = it.findViewById(R.id.bottom_navigation)
            bottomNavigationMenu = bottomNavigationView.menu

            with(it.resources) {
                menuStringContent = hashMapOf(
                    R.string.menu_item_view to getString(R.string.menu_item_view),
                    R.string.menu_item_save to getString(R.string.menu_item_save),
                    R.string.menu_item_generate_pass to getString(R.string.menu_item_generate_pass)
                )
            }
        }
    }

    @After
    fun teardown() {
        scenario.onActivity {
            val preferences = it.getPreferences(Context.MODE_PRIVATE)
            with(preferences.edit()) {
                putBoolean(AUTHENTICATED_KEY, false)
                apply()
            }
        }
    }

    @Test
    fun bottomNavigationBar_displaysCorrectMenuItems() {
        for (i in 0 until bottomNavigationMenu.size()) {
            val menuItem = bottomNavigationMenu.getItem(i)
            Assert.assertEquals(MENU_ITEM_IDS[i], menuItem.itemId)
        }
    }

    companion object {
        private val MENU_ITEM_IDS = listOf(
            R.id.view_accounts_fragment,
            R.id.save_account_fragment,
            R.id.generate_password_fragment
        )
    }
}