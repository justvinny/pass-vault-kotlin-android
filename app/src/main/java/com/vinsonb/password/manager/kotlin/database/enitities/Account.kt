package com.vinsonb.password.manager.kotlin.database.enitities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.vinsonb.password.manager.kotlin.utilities.Constants.Csv.DELIMITER
import java.util.*

@Entity(primaryKeys = ["platform", "username"])
class Account(
    @ColumnInfo val platform: String,
    @ColumnInfo val username: String,
    @ColumnInfo val password: String
) {
    fun toCsvString(): String {
        return "$platform$DELIMITER$username$DELIMITER$password"
    }

    override fun equals(other: Any?): Boolean {
        return other is Account &&
                platform == other.platform &&
                username == other.username &&
                password == other.password
    }

    override fun hashCode(): Int {
        return Objects.hash(platform, username, password)
    }

    companion object {
        const val NUM_FIELDS = 3
    }
}