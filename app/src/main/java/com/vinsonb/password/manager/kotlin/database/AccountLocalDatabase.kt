package com.vinsonb.password.manager.kotlin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vinsonb.password.manager.kotlin.database.dao.AccountDao
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.utilities.Constants.Companion.Database.DATABASE_NAME

@Database(entities = [Account::class], version = 1)
abstract class AccountLocalDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AccountLocalDatabase? = null

        fun getDatabase(context: Context): AccountLocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, AccountLocalDatabase::class.java, DATABASE_NAME)
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}