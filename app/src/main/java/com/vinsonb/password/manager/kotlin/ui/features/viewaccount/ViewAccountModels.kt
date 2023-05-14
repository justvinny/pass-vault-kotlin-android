package com.vinsonb.password.manager.kotlin.ui.features.viewaccount

import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider
import com.vinsonb.password.manager.kotlin.utilities.textResIdProvider

data class ViewAccountState(
    val accounts: List<Account> = emptyList(),
    val searchQuery: String = "",
    val selectedAccount: Account? = null,
)

sealed interface ViewAccountToastState {
    object SuccessfullyDeleted : ViewAccountToastState,
        TextResIdProvider by textResIdProvider(R.string.success_deleted_account)

    object SuccessfullyUpdated : ViewAccountToastState,
        TextResIdProvider by textResIdProvider(R.string.success_updated_account)

    object FailedAccountUpdate : ViewAccountToastState,
        TextResIdProvider by textResIdProvider(R.string.error_update_unsuccessful)

    object FailedUsernameUpdate : ViewAccountToastState,
        TextResIdProvider by textResIdProvider(R.string.error_save_unsuccessful)

    object None : ViewAccountToastState
}
