package com.vinsonb.password.manager.kotlin.di

import android.content.Context
import androidx.room.Room
import com.vinsonb.password.manager.kotlin.BuildConfig
import com.vinsonb.password.manager.kotlin.database.AccountLocalDatabase
import com.vinsonb.password.manager.kotlin.database.AccountRepository
import com.vinsonb.password.manager.kotlin.utilities.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun providesAccountLocalDatabase(@ApplicationContext context: Context): AccountLocalDatabase {
        val passphrase = BuildConfig.ROOM_PASSPHRASE
        val factory = SupportFactory(passphrase.toByteArray())
        return Room
            .databaseBuilder(
                context, AccountLocalDatabase::class.java,
                Constants.Database.DATABASE_NAME
            )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }


    @Singleton
    @Provides
    fun providesAccountRepository(database: AccountLocalDatabase): AccountRepository =
        AccountRepository(database)
}