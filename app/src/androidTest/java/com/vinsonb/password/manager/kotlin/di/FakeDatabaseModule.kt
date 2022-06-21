package com.vinsonb.password.manager.kotlin.di

import android.content.Context
import androidx.room.Room
import com.vinsonb.password.manager.kotlin.database.AccountLocalDatabase
import com.vinsonb.password.manager.kotlin.database.AccountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object FakeDatabaseModule {
    private lateinit var accountLocalDatabase: AccountLocalDatabase

    @Singleton
    @Provides
    fun providesFakeAccountLocalDatabase(@ApplicationContext context: Context): AccountLocalDatabase =
        Room
            .inMemoryDatabaseBuilder(context, AccountLocalDatabase::class.java)
            .build()
            .also {
                accountLocalDatabase = it
            }

    @Singleton
    @Provides
    fun providesFakeAccountRepository(database: AccountLocalDatabase): AccountRepository =
        AccountRepository(database)

    suspend fun populateFakeData() {
        if (this::accountLocalDatabase.isInitialized) {
            FakeData.FAKE_ACCOUNTS.forEach {
                accountLocalDatabase.accountDao().insertAccount(it)
            }
        }
    }
}