package com.vinsonb.password.manager.kotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vinsonb.password.manager.kotlin.database.AccountRepository
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import kotlinx.coroutines.launch

class AccountViewModel(private val accountRepository: AccountRepository): ViewModel() {
    // TODO in View Accounts Ticket
    val accounts: LiveData<List<Account>> = accountRepository.getAll().asLiveData()

    suspend fun insertAccount(account: Account) {
        accountRepository.insertAccount(account)
    }

    // TODO in Update Account Ticket
    fun updateAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.updateAccount(account)
        }
    }

    // TODO in Delete Account Ticket
    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.deleteAccount(account)
        }
    }
}