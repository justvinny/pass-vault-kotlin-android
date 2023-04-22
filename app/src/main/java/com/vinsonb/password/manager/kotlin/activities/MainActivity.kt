package com.vinsonb.password.manager.kotlin.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.databinding.ActivityMainBinding
import com.vinsonb.password.manager.kotlin.extensions.SESSION_EXPIRED_KEY
import com.vinsonb.password.manager.kotlin.extensions.hasSessionExpired
import com.vinsonb.password.manager.kotlin.ui.features.credits.CreditsDialog
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.Constants
import com.vinsonb.password.manager.kotlin.utilities.Constants.MimeType.CSV
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import com.vinsonb.password.manager.kotlin.viewmodels.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var accounts: List<Account> = listOf()
    private var isDialogShown by mutableStateOf(false)
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navController: NavController
    private lateinit var csvLauncher: ActivityResultLauncher<String>
    private lateinit var createCsvLauncher: ActivityResultLauncher<String>

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        csvLauncher = getCsvContent()
        createCsvLauncher = createDocumentCsv()

        setContent {
            AndroidViewBinding(ActivityMainBinding::inflate) {
                binding = this@AndroidViewBinding

                // Setup for bottom navigation bar to use navigation controller.
                navController = binding.navHostFragment.findNavController()
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
                        R.id.menu_item_import_csv -> {
                            csvLauncher.launch(CSV)
                            true
                        }
                        R.id.menu_item_export_csv -> {
                            createCsvLauncher.launch(Constants.FileName.DEFAULT_FILENAME)
                            true
                        }
                        R.id.menu_item_credits -> {
                            isDialogShown = true
                            true
                        }
                        R.id.menu_item_logout -> {
                            logout()
                            true
                        }
                        else -> false
                    }
                }

                viewModel.accounts.observe(this@MainActivity) {
                    accounts = it
                }
            }

            if (isDialogShown) {
                PassVaultTheme {
                    CreditsDialog {
                        isDialogShown = false
                    }
                }
            }

            LaunchedEffect(Unit) {
                logoutIfAuthenticationExpired()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        logoutIfAuthenticationExpired()
    }

    override fun onPause() {
        super.onPause()
        saveTimeNowToPreferences()
        removeAuthenticationOnFinish()
    }

    /**
     * Launcher to use when loading CSV content from the system's file picker.
     */
    private fun getCsvContent() = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) viewModel.loadAccountsFromCsv(contentResolver, it, accounts)
    }

    /**
     * Launcher to use when saving Accounts to a CSV file using the system's file picker.
     */
    private fun createDocumentCsv() =
        registerForActivityResult(ActivityResultContracts.CreateDocument(CSV)) {
            if (it != null) viewModel.saveAccountsAsCsv(contentResolver, it, accounts)
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
            navController.navigate(R.id.login_fragment)
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
     * Save [LocalTime.now] to shared preferences which will be used to determine if the user should
     * be logout due to them being away from the app for x amount of time which is determine
     * in [LocalTime.hasSessionExpired].
     */
    private fun saveTimeNowToPreferences() {
        with(sharedPreferences.edit()) {
            putString(SESSION_EXPIRED_KEY, LocalTime.now().toString())
            apply()
        }
    }

    /**
     * Logout the user if the app [isFinishing].
     */
    private fun removeAuthenticationOnFinish() {
        if (isFinishing) {
            removeAuthenticatedStatus()
        }
    }

    /**
     * Logouts the user if authentication has expired due to time elapsed.
     */
    private fun logoutIfAuthenticationExpired() {
        if (!this::navController.isInitialized) return
        Log.d(this::class.simpleName, "NavController initialised")
        val isAuthenticated = sharedPreferences.getBoolean(AUTHENTICATED_KEY, false)
        val hasSessionExpired = sharedPreferences.getString(SESSION_EXPIRED_KEY, "")?.let {
            it.isNotBlank() && LocalTime.now().hasSessionExpired(it)
        } ?: false

        if (!isAuthenticated || hasSessionExpired) {
            logout()
        }
    }
}
