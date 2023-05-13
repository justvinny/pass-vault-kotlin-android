package com.vinsonb.password.manager.kotlin.ui.features.viewaccount

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.database.AccountRepository
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.di.CoroutineDispatchers
import com.vinsonb.password.manager.kotlin.extensions.stateIn
import com.vinsonb.password.manager.kotlin.utilities.EventFlow
import com.vinsonb.password.manager.kotlin.utilities.eventFlowDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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
    private val insertAccount: suspend (Account) -> Boolean,
    private val updateAccount: suspend (Account) -> Boolean,
    private val deleteAccount: suspend (Account) -> Boolean,
) : ViewModel(), EventFlow<ViewAccountToastState> by eventFlowDelegate(scope) {
    @Inject
    constructor(
        accountRepository: AccountRepository,
        dispatchers: CoroutineDispatchers,
    ) : this(
        scope = CoroutineScope(dispatchers.default),
        getAllAccounts = accountRepository::getAll,
        insertAccount = accountRepository::insertAccount,
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

    fun onUpdateAccount(account: Account, originalAccount: Account) {
        scope.launch {
            val hasSucceeded = if (originalAccount.username != account.username) {
                handleUsernameChange(account, originalAccount)
            } else {
                handlePasswordOnlyChange(account)
            }

            if (hasSucceeded) {
                sendEvent(ViewAccountToastState.SuccessfullyUpdated)
            }
        }
    }

    private suspend fun handleUsernameChange(account: Account, originalAccount: Account): Boolean {
        val isInserted = insertAccount(account)
        val isDeleted = isInserted && deleteAccount(originalAccount)

        if (!isInserted) {
            sendEvent(ViewAccountToastState.FailedUsernameUpdate)
        } else if (!isDeleted) {
            deleteAccount(account)
            sendEvent(ViewAccountToastState.FailedAccountUpdate)
        } else {
            onSelectAccount(account)
        }

        return isInserted && isDeleted
    }

    private suspend fun handlePasswordOnlyChange(account: Account): Boolean {
        return updateAccount(account).also { isUpdated ->
            if (!isUpdated) {
                sendEvent(ViewAccountToastState.FailedAccountUpdate)
            }
        }
    }

    fun onDeleteAccount(account: Account) {
        scope.launch {
            if (deleteAccount(account)) {
                sendEvent(ViewAccountToastState.SuccessfullyDeleted)
            }
        }
    }

    fun onClearSearch() {
        _stateFlow.update { it.copy(searchQuery = "") }
    }
}
