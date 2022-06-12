package com.vinsonb.password.manager.kotlin.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.AccountLocalDatabase
import com.vinsonb.password.manager.kotlin.database.AccountRepository
import com.vinsonb.password.manager.kotlin.databinding.ActivityMainBinding
import com.vinsonb.password.manager.kotlin.viewmodels.AccountViewModel

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val accountLocalDatabase = AccountLocalDatabase.getDatabase(this)
        val accountRepository = AccountRepository(accountLocalDatabase)
        accountViewModel = AccountViewModel(accountRepository)

        // Setup for bottom navigation bar to use navigation controller.
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Hide bottom navigation bar if not authenticated.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.login_fragment, R.id.create_login_fragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }

    fun getAccountViewModel(): AccountViewModel {
        return accountViewModel
    }
}