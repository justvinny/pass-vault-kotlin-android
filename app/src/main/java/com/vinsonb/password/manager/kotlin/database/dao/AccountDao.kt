package com.vinsonb.password.manager.kotlin.database.dao

import androidx.room.*
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM account")
    fun getAll(): Flow<List<Account>>

    @Insert
    suspend fun insertAccount(account: Account)

    @Update
    suspend fun updateAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)
}