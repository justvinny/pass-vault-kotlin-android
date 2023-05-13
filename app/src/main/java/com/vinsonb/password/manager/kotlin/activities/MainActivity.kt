package com.vinsonb.password.manager.kotlin.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.di.CoroutineModule
import com.vinsonb.password.manager.kotlin.extensions.SESSION_EXPIRED_KEY
import com.vinsonb.password.manager.kotlin.extensions.hasSessionExpired
import com.vinsonb.password.manager.kotlin.extensions.showToast
import com.vinsonb.password.manager.kotlin.ui.features.bottomnavmenu.BottomNavMenu
import com.vinsonb.password.manager.kotlin.ui.features.createlogin.CreateLoginScreen
import com.vinsonb.password.manager.kotlin.ui.features.createlogin.CreateLoginViewModel
import com.vinsonb.password.manager.kotlin.ui.features.credits.CreditsDialog
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeDialog
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeState
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeViewModel
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginScreen
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginViewModel
import com.vinsonb.password.manager.kotlin.ui.features.navigation.NavigationDestination
import com.vinsonb.password.manager.kotlin.ui.features.navigation.NavigationDestination.*
import com.vinsonb.password.manager.kotlin.ui.features.passwordgenerator.PasswordGeneratorScreen
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountScreen
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountViewModel
import com.vinsonb.password.manager.kotlin.ui.features.topnavmenu.TopNavMenu
import com.vinsonb.password.manager.kotlin.ui.features.topnavmenu.TopNavMenuItem
import com.vinsonb.password.manager.kotlin.ui.features.viewaccount.ViewAccountScreen
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.Constants
import com.vinsonb.password.manager.kotlin.utilities.Constants.MimeType.CSV
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEvent
import com.vinsonb.password.manager.kotlin.viewmodels.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var accounts: List<Account> = listOf()
    private var isCreditsDialogVisible by mutableStateOf(false)
    private var isBottomNavMenuVisible by mutableStateOf(true)
    private var isTopNavMenuVisible by mutableStateOf(true)
    private var isTopNavMenuItemsVisible by mutableStateOf(true)
    private var topNavMenuTitle by mutableStateOf("")
    private var navController: NavController? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var csvLauncher: ActivityResultLauncher<String>
    private lateinit var createCsvLauncher: ActivityResultLauncher<String>

    private val topNavMenuItems = arrayOf(
        TopNavMenuItem(
            itemNameRes = R.string.menu_item_import_csv,
            action = ::importCsv,
        ),
        TopNavMenuItem(
            itemNameRes = R.string.menu_item_export_csv,
            action = ::exportCsv,
        ),
        TopNavMenuItem(
            itemNameRes = R.string.menu_item_credits,
            action = ::showCreditsDialog,
        ),
        TopNavMenuItem(
            itemNameRes = R.string.menu_item_logout,
            action = ::logout,
        ),
    )

    private val accountViewModel: AccountViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val saveAccountViewModel: SaveAccountViewModel by viewModels()
    private val createLoginViewModel by lazy {
        CreateLoginViewModel(
            scope = CoroutineScope(CoroutineModule.providesCoroutineDispatchers().default),
            _createLogin = ::saveLoginDetails,
        )
    }
    private val forgotPasscodeViewModel by lazy {
        ForgotPasscodeViewModel(
            dispatchers = CoroutineModule.providesCoroutineDispatchers(),
            savedSecretAnswer = getSecretAnswer(),
            saveNewPasscode = ::saveNewPasscode,
        )
    }

    // region start lifecycle methods

    override fun onResume() {
        super.onResume()
        if (!isAccountNotCreated()) {
            logoutIfAuthenticationExpired()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isAccountNotCreated()) {
            saveTimeNowToPreferences()
            removeAuthenticationOnFinish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        csvLauncher = getCsvContent()
        createCsvLauncher = createDocumentCsv()

        accountViewModel.accounts.observe(this) {
            accounts = it
        }

        lifecycleScope.launch {
            saveAccountViewModel.eventFlow.collect {
                when (it) {
                    SimpleToastEvent.None -> {}
                    SimpleToastEvent.ShowFailed ->
                        applicationContext.showToast(R.string.error_save_unsuccessful)
                    SimpleToastEvent.ShowSucceeded ->
                        applicationContext.showToast(R.string.success_save_account)
                }
            }
        }

        lifecycleScope.launch {
            loginViewModel.stateFlow.collect { state ->
                if (state.passcodeLength == Constants.Password.PASSCODE_MAX_LENGTH) {
                    if (passcodeMatches(state.passcode)) {
                        login()
                    } else {
                        applicationContext.showToast(R.string.error_wrong_passcode)
                    }

                    loginViewModel.onClearAllDigits()
                }
            }
        }

        lifecycleScope.launch {
            forgotPasscodeViewModel.eventFlow.collect {
                when (it) {
                    SimpleToastEvent.None -> {}
                    SimpleToastEvent.ShowFailed ->
                        applicationContext.showToast(R.string.error_reset_unsuccessful)
                    SimpleToastEvent.ShowSucceeded ->
                        applicationContext.showToast(R.string.success_passcode_reset)
                }
            }
        }

        setContent {
            PassVaultTheme {
                Scaffold(
                    topBar = {
                        if (isTopNavMenuVisible) {
                            TopNavMenu(
                                title = topNavMenuTitle,
                                menuItems = topNavMenuItems,
                                isMenuVisible = isTopNavMenuItemsVisible,
                            )
                        }
                    },
                    bottomBar = {
                        if (isBottomNavMenuVisible) {
                            BottomNavMenu(navigateTo = ::navigateTo)
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        val navController = rememberNavController()
                        this@MainActivity.navController = navController

                        navController.addOnDestinationChangedListener { _, destination, _ ->
                            destination.route?.also { route ->
                                toggleNavBarsOnNavigate(route)
                                changeTitleOnNavigate(route)
                            }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = getStartDestination(),
                        ) {
                            composable(CREATE_LOGIN.destination) {
                                CreateLoginScreen(createLoginViewModel)
                            }

                            composable(LOGIN.destination) {
                                LoginScreen(loginViewModel, forgotPasscodeViewModel::showDialog)
                            }

                            composable(VIEW_ACCOUNTS.destination) {
                                ViewAccountScreen(hiltViewModel())
                            }

                            composable(SAVE_ACCOUNT.destination) {
                                SaveAccountScreen(saveAccountViewModel)
                            }

                            composable(GENERATE_PASSWORD.destination) {
                                PasswordGeneratorScreen()
                            }
                        }
                    }
                }

                if (isCreditsDialogVisible) {
                    CreditsDialog {
                        isCreditsDialogVisible = false
                    }
                }

                val isDialogShown by forgotPasscodeViewModel.stateFlow.collectAsState()
                if (isDialogShown is ForgotPasscodeState.Visible) {
                    ForgotPasscodeDialog(
                        viewModel = forgotPasscodeViewModel,
                        secretQuestion = getSecretQuestion(),
                    )
                }
            }
        }
    }

    // region end lifecycle methods

    // region start navigation methods

    private fun getStartDestination(): String {
        return when {
            isAccountNotCreated() -> CREATE_LOGIN
            isAuthenticationExpired() -> LOGIN
            else -> VIEW_ACCOUNTS
        }.destination
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
            navigateTo(LOGIN, true)
        }
    }

    /**
     * Logouts the user if authentication has expired due to time elapsed.
     */
    private fun logoutIfAuthenticationExpired() {
        if (isAuthenticationExpired()) {
            logout()
        }
    }

    private fun navigateTo(navDestination: NavigationDestination, popBackStack: Boolean = false) {
        navController?.run {
            if (popBackStack) {
                popBackStack()
            }

            navigate(navDestination.destination) {
                launchSingleTop = true
            }

            Log.d(
                this::class.java.simpleName,
                "Test test $navDestination $isTopNavMenuVisible $isBottomNavMenuVisible"
            )
        }
    }

    private fun toggleNavBarsOnNavigate(currentDestination: String) {
        when (currentDestination) {
            CREATE_LOGIN.destination -> {
                isTopNavMenuVisible = true
                isTopNavMenuItemsVisible = false
                isBottomNavMenuVisible = false
            }
            LOGIN.destination -> {
                isTopNavMenuVisible = false
                isBottomNavMenuVisible = false
            }
            else -> {
                isTopNavMenuVisible = true
                isTopNavMenuItemsVisible = true
                isBottomNavMenuVisible = true
            }
        }
    }

    private fun changeTitleOnNavigate(currentDestination: String) {
        topNavMenuTitle = when (currentDestination) {
            CREATE_LOGIN.destination -> {
                getString(R.string.title_create_login)
            }
            VIEW_ACCOUNTS.destination -> {
                getString(R.string.title_view_accounts)
            }
            SAVE_ACCOUNT.destination -> {
                getString(R.string.title_save_account)
            }
            GENERATE_PASSWORD.destination -> {
                getString(R.string.title_generate_password)
            }
            else -> ""
        }
    }

    private fun showCreditsDialog() {
        isCreditsDialogVisible = true
    }

    // region end navigation methods

    // region start SharedPreferences methods

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

    private fun isAuthenticationExpired(): Boolean {
        val isAuthenticated = sharedPreferences.getBoolean(AUTHENTICATED_KEY, false)
        val hasSessionExpired = sharedPreferences.getString(SESSION_EXPIRED_KEY, "")?.let {
            when {
                it.isBlank() -> true
                else -> LocalTime.now().hasSessionExpired(it)
            }
        } ?: true

        return !isAuthenticated || hasSessionExpired
    }

    private fun saveLoginDetails(passcode: String, secretQuestion: String, secretAnswer: String) {
        with(sharedPreferences.edit()) {
            putString(
                Constants.Password.SharedPreferenceKeys.PASSCODE_KEY,
                passcode
            )
            putString(
                Constants.Password.SharedPreferenceKeys.SECRET_QUESTION_KEY,
                secretQuestion
            )
            putString(
                Constants.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY,
                secretAnswer
            )
            if (commit()) {
                navigateTo(LOGIN, true)
            } else {
                applicationContext.showToast(R.string.error_save_unsuccessful)
            }
        }
    }

    private fun passcodeMatches(passcode: String): Boolean {
        return sharedPreferences.getString(
            Constants.Password.SharedPreferenceKeys.PASSCODE_KEY,
            ""
        ) == passcode
    }

    private fun login() {
        val isSuccessful = with(sharedPreferences.edit()) {
            putBoolean(AUTHENTICATED_KEY, true)
            commit()
        }

        if (isSuccessful) {
            navigateTo(VIEW_ACCOUNTS, true)
        }
    }

    private fun getSecretAnswer(): String {
        return sharedPreferences.getString(
            Constants.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY,
            "",
        ) ?: ""
    }

    private fun getSecretQuestion(): String {
        return sharedPreferences.getString(
            Constants.Password.SharedPreferenceKeys.SECRET_QUESTION_KEY,
            "",
        ) ?: ""
    }

    private fun saveNewPasscode(newPasscode: String): Boolean {
        return with(sharedPreferences.edit()) {
            putString(
                Constants.Password.SharedPreferenceKeys.PASSCODE_KEY,
                newPasscode
            )
            commit()
        }
    }

    private fun isAccountNotCreated(): Boolean {
        val passcode = sharedPreferences.getString(
            Constants.Password.SharedPreferenceKeys.PASSCODE_KEY,
            ""
        )
        val secretQuestion = sharedPreferences.getString(
            Constants.Password.SharedPreferenceKeys.SECRET_QUESTION_KEY,
            ""
        )
        val secretAnswer = sharedPreferences.getString(
            Constants.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY,
            ""
        )
        return passcode.isNullOrBlank()
                && secretQuestion.isNullOrBlank()
                && secretAnswer.isNullOrBlank()
    }

    // region end SharedPreferences methods

    // region start CSV methods

    /**
     * Launcher to use when loading CSV content from the system's file picker.
     */
    private fun getCsvContent() = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) accountViewModel.loadAccountsFromCsv(contentResolver, it, accounts)
    }

    /**
     * Launcher to use when saving Accounts to a CSV file using the system's file picker.
     */
    private fun createDocumentCsv() =
        registerForActivityResult(ActivityResultContracts.CreateDocument(CSV)) {
            if (it != null) accountViewModel.saveAccountsAsCsv(contentResolver, it, accounts)
        }

    private fun importCsv() {
        if (::csvLauncher.isInitialized) {
            csvLauncher.launch(CSV)
        }
    }

    private fun exportCsv() {
        if (::createCsvLauncher.isInitialized) {
            createCsvLauncher.launch(Constants.FileName.DEFAULT_FILENAME)
        }
    }

    // region end CSV methods
}
