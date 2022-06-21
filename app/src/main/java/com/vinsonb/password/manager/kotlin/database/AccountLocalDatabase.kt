package com.vinsonb.password.manager.kotlin.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vinsonb.password.manager.kotlin.database.dao.AccountDao
import com.vinsonb.password.manager.kotlin.database.enitities.Account

@Database(entities = [Account::class], version = 1)
abstract class AccountLocalDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}