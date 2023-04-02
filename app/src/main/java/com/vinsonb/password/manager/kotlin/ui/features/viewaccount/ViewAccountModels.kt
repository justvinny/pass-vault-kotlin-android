package com.vinsonb.password.manager.kotlin.ui.features.viewaccount

import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider
import com.vinsonb.password.manager.kotlin.utilities.textResIdProvider

data class ViewAccountState(
    val accounts: List<Account>,
    val selectedAccount: Account? = null,
    val toastState: ViewAccountToastState = ViewAccountToastState.Idle,
)

sealed interface ViewAccountToastState {
    object SuccessfullyDeleted : ViewAccountToastState,
        TextResIdProvider by textResIdProvider(R.string.success_deleted_account)

    object SuccessfullyUpdated : ViewAccountToastState,
        TextResIdProvider by textResIdProvider(R.string.success_updated_password)

    object Idle : ViewAccountToastState
}
