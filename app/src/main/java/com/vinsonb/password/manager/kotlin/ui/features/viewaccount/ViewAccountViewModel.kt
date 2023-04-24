package com.vinsonb.password.manager.kotlin.ui.features.viewaccount

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.database.AccountRepository
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.di.CoroutineDispatchers
import com.vinsonb.password.manager.kotlin.extensions.stateIn
import com.vinsonb.password.manager.kotlin.utilities.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewAccountViewModel(
    private val scope: CoroutineScope,
    private val getAllAccounts: () -> Flow<List<Account>>,
    private val updateAccount: suspend (Account) -> Boolean,
    private val deleteAccount: suspend (Account) -> Boolean,
) : ViewModel() {
    @Inject
    constructor(
        accountRepository: AccountRepository,
        dispatchers: CoroutineDispatchers,
    ) : this(
        scope = CoroutineScope(dispatchers.default),
        getAllAccounts = accountRepository::getAll,
        updateAccount = accountRepository::updateAccount,
        deleteAccount = accountRepository::deleteAccount,
    )

    private val accountsFlow = getAllAccounts()
    private val _stateFlow = MutableStateFlow(ViewAccountState())
    val stateFlow = combine(
        accountsFlow,
        _stateFlow,
    ) { accounts, state ->
        state.copy(accounts = accounts.filterByPlatformOrUsername(state.searchQuery))
    }
        .stateIn(scope = scope, initialValue = ViewAccountState())

    fun onSearch(query: String) {
        _stateFlow.update { it.copy(searchQuery = query) }
    }

    private fun List<Account>.filterByPlatformOrUsername(query: String) = filter {
        it.username.contains(query, true) || it.platform.contains(query, true)
    }

    fun onSelectAccount(account: Account) {
        _stateFlow.update { it.copy(selectedAccount = account) }
    }

    fun onUpdateAccount(account: Account) {
        scope.launch {
            if (updateAccount(account)) {
                _stateFlow.update { it.copy(toastState = ViewAccountToastState.SuccessfullyUpdated) }
                changeToIdleAfterDelay()
            }
        }
    }

    fun onDeleteAccount(account: Account) {
        scope.launch {
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
        _stateFlow.update { it.copy(searchQuery = "") }
    }
}
