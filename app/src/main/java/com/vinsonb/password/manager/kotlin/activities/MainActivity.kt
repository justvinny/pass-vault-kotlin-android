package com.vinsonb.password.manager.kotlin.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.databinding.ActivityMainBinding
import com.vinsonb.password.manager.kotlin.fragments.dialogs.CreditsDialog
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.Timer.MAX_TIMER_MILLI
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.Timer.TIMER_INTERVAL_MILLI
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivityPassVault"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var isTimedOut = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var creditsDialog: CreditsDialog
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        countDownTimer = createCountdownTimer()
        creditsDialog = CreditsDialog()

        // Setup for bottom navigation bar to use navigation controller.
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Hide navigation bars if not authenticated.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.login_fragment -> {
                    binding.topNavigation.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.GONE
                }
                R.id.create_login_fragment -> {
                    binding.topNavigation.visibility = View.VISIBLE
                    binding.topNavigation.menu.forEach { it.isVisible = false }
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> {
                    binding.topNavigation.visibility = View.VISIBLE
                    binding.topNavigation.menu.forEach { it.isVisible = true }
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }
        }

        // Top Bar Menu Items
        binding.topNavigation.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_credits -> {
                    creditsDialog.show(supportFragmentManager, CreditsDialog.TAG)
                    true
                }
                R.id.menu_item_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkIfAuthenticationTimedOut()
    }

    override fun onStop() {
        super.onStop()
        countDownTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        logout()
    }

    /**
     * Logs out the user using [removeAuthenticatedStatus].
     *
     * Also navigates to the ViewAccountsFragment if current fragment is a different top-level
     * destination. This is because our main logout navigation logic is contained in the
     * ViewAccountsFragment.
     */
    private fun logout() {
        if (removeAuthenticatedStatus()) {
            navController.popBackStack()
            navController.navigate(R.id.view_accounts_fragment)
        }
    }

    /**
     * Change authenticated value to false in SharedPreferences.
     *
     * returns whether value was successfully changed to false or not.
     */
    private fun removeAuthenticatedStatus(): Boolean {
        with(sharedPreferences.edit()) {
            this?.putBoolean(AUTHENTICATED_KEY, false)
            return commit()
        }
    }

    /**
     * Checks if the current user's authentication timed out.
     * If [isTimedOut] evaluates to true, logout the user.
     * Otherwise, cancel the [countDownTimer].
     */
    private fun checkIfAuthenticationTimedOut() {
        if (isTimedOut) {
            logout()
            isTimedOut = false
        } else {
            countDownTimer.cancel()
        }
    }

    /**
     * Countdown timer to be used for logging out when a user has the activity on the STOPPED state.
     * Timer is set to [MAX_TIMER_MILLI] with an interval of [TIMER_INTERVAL_MILLI].
     * Boolean [isTimedOut] will be set to true when countdown finishes.
     */
    private fun createCountdownTimer() =
        object : CountDownTimer(MAX_TIMER_MILLI, TIMER_INTERVAL_MILLI) {
            override fun onTick(millis: Long) {}

            override fun onFinish() {
                isTimedOut = true
            }
        }
}