package com.vinsonb.password.manager.kotlin.database.dao

import androidx.room.*
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM account")
    fun getAll(): Flow<List<Account>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccount(account: Account): Long

    @Update
    suspend fun updateAccount(account: Account): Int

    @Delete
    suspend fun deleteAccount(account: Account): Int
}