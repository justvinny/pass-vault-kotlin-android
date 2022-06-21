package com.vinsonb.password.manager.kotlin.di

import com.vinsonb.password.manager.kotlin.database.enitities.Account

object FakeData {
    val FAKE_ACCOUNTS = listOf(
        Account("Amazon", "john.doe", "123456789"),
        Account("Netflix", "steph.curry", "4121251"),
        Account("Facebook", "andrew.wiggins", "gvzsdgv"),
        Account("Google", "luka.doncic", "jhgjkg312"),
        Account("Microsoft", "jordan.poole", "nbvbvmv432"),
        Account("Apple", "jonathan.kuminga", "769gfhd54345df"),
    )
}