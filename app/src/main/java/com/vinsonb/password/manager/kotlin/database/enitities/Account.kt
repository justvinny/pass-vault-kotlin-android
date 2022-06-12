package com.vinsonb.password.manager.kotlin.database.enitities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["platform", "username"])
data class Account(
    @NonNull @ColumnInfo val platform: String,
    @NonNull @ColumnInfo val username: String,
    @NonNull @ColumnInfo val password: String
)