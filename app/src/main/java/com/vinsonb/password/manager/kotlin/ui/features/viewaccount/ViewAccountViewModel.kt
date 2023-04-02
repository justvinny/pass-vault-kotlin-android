package com.vinsonb.password.manager.kotlin.ui.features.viewaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinsonb.password.manager.kotlin.database.AccountRepository
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.utilities.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewAccountViewModel(
    private val getAllAccounts: () -> Flow<List<Account>>,
    private val updateAccount: suspend (Account) -> Boolean,
    private val deleteAccount: suspend (Account) -> Boolean,
) : ViewModel() {
    @Inject
    constructor(
        accountRepository: AccountRepository,
    ) : this(
        getAllAccounts = accountRepository::getAll,
        updateAccount = accountRepository::updateAccount,
        deleteAccount = accountRepository::deleteAccount,
    )

    private val accountsFlow = getAllAccounts()
    private val searchQueryFlow = MutableStateFlow("")
    private val _stateFlow = MutableStateFlow(ViewAccountState(emptyList()))
    val stateFlow = combine(
        accountsFlow,
        searchQueryFlow,
        _stateFlow,
    ) { accounts, searchQuery, initialState ->
        initialState.copy(accounts = accounts.filterByPlatformOrUsername(searchQuery))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ViewAccountState(emptyList()),
    )

    fun onSearch(query: String) {
        searchQueryFlow.update { query }
    }

    private fun List<Account>.filterByPlatformOrUsername(query: String) = filter {
        it.username.contains(query, true) || it.platform.contains(query, true)
    }

    fun onSelectAccount(account: Account) {
        _stateFlow.update { it.copy(selectedAccount = account) }
    }

    fun onUpdateAccount(account: Account) {
        viewModelScope.launch {
            if (updateAccount(account)) {
                _stateFlow.update { it.copy(toastState = ViewAccountToastState.SuccessfullyUpdated) }
                changeToIdleAfterDelay()
            }
        }
    }

    fun onDeleteAccount(account: Account) {
        viewModelScope.launch {
            if (deleteAccount(account)) {
                _stateFlow.update { it.copy(toastState = ViewAccountToastState.SuccessfullyDeleted) }
                changeToIdleAfterDelay()
            }
        }
    }

    private suspend fun changeToIdleAfterDelay() {
        delay(Constants.Toast.SHORT_DELAY)
        _stateFlow.update { it.copy(toastState = ViewAccountToastState.Idle) }
    }

    fun onClearSearch() {
        searchQueryFlow.update { "" }
    }
}
