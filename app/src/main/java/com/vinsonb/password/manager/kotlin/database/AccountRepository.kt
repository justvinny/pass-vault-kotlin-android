package com.vinsonb.password.manager.kotlin.database

import com.vinsonb.password.manager.kotlin.database.dao.AccountDao
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import kotlinx.coroutines.flow.Flow

class AccountRepository(
    private val localDatabase: AccountLocalDatabase,
    private val accountDao: AccountDao = localDatabase.accountDao()
) {
    fun getAll(): Flow<List<Account>> {
        return accountDao.getAll()
    }

    suspend fun insertAccount(account: Account): Boolean {
        return accountDao.insertAccount(account) != SQLITE_INSERT_FAILED_VALUE
    }

    suspend fun updateAccount(account: Account): Boolean {
        return accountDao.updateAccount(account) > 0
    }

    suspend fun deleteAccount(account: Account): Boolean {
        return accountDao.deleteAccount(account) > 0
    }

    companion object {
        private const val SQLITE_INSERT_FAILED_VALUE = -1L
    }
}