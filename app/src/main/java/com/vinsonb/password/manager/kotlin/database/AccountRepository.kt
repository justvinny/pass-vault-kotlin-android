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

    suspend fun insertAccount(account: Account) {
        accountDao.insertAccount(account)
    }

    suspend fun updateAccount(account: Account) {
        accountDao.updateAccount(account)
    }

    suspend fun deleteAccount(account: Account) {
        accountDao.deleteAccount(account)
    }
}