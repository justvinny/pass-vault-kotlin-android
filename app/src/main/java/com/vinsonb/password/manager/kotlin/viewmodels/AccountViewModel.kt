package com.vinsonb.password.manager.kotlin.viewmodels

import android.content.ContentResolver
import android.net.Uri
import android.util.Log.e
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vinsonb.password.manager.kotlin.database.AccountRepository
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.database.enitities.Account.Companion.NUM_FIELDS
import com.vinsonb.password.manager.kotlin.utilities.Constants.Csv.DELIMITER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    val accounts: LiveData<List<Account>> = accountRepository.getAll().asLiveData()

    /**
     * Inserts a new [account] to our Room Database.
     */
    private fun insertAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.insertAccount(account)
        }
    }

    /**
     * Updates password of given [account] in our Room Database.
     */
    fun updateAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.updateAccount(account)
        }
    }

    /**
     * Deletes given [account] from our Room Database.
     */
    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.deleteAccount(account)
        }
    }

    /**
     * Saves all accounts to a CSV file using an OutputStream created with a [contentResolver] and [uri].
     */
    fun saveAccountsAsCsv(contentResolver: ContentResolver, uri: Uri, accounts: List<Account>) {
        viewModelScope.launch {
            runCatching {
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    val stringBuilder = StringBuilder()
                    accounts.forEach { account ->
                        stringBuilder.append(account.toCsvString()).append("\n")
                    }
                    outputStream.write(stringBuilder.toString().toByteArray())
                    outputStream.close()
                }
            }.onFailure {
                e(TAG, it.message.toString())
            }
        }
    }

    /**
     * Loads accounts from a CSV file using an InputStream created with a [contentResolver] and [uri].
     */
    fun loadAccountsFromCsv(contentResolver: ContentResolver, uri: Uri, accounts: List<Account>) {
        viewModelScope.launch {
            runCatching {
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val bytes = inputStream.readBytes()
                    inputStream.close()
                    val string = String(bytes, StandardCharsets.UTF_8)
                    val stringArray = string.split("\n")
                    stringArray.forEach { csvAccount ->
                        if (csvAccount.isNotBlank()) {
                            val accountSplit = csvAccount.split(DELIMITER)
                            if (accountSplit.size == NUM_FIELDS) {
                                val account = Account(accountSplit[0], accountSplit[1], accountSplit[2])
                                if (!accounts.contains(account)) {
                                    insertAccount(account)
                                }
                            }
                        }
                    }
                }
            }.onFailure {
                e(TAG, it.message.toString())
            }
        }
    }

    companion object {
        val TAG = this::class.simpleName
    }
}