package com.vinsonb.password.manager.kotlin.extensions

import java.time.LocalTime

const val SESSION_EXPIRED_KEY = "sessionExpiredKey"
private const val MINUTES_IN_HOUR = 60
private const val MAX_SESSION_MINUTES = 10

fun LocalTime.hasSessionExpired(oldTime: String): Boolean {
    val oldTimeInMinutesOfDay = LocalTime.parse(oldTime).toSecondOfDay() / MINUTES_IN_HOUR
    val timeNowInMinutesOfDay = this.toSecondOfDay() / MINUTES_IN_HOUR
    return (timeNowInMinutesOfDay - oldTimeInMinutesOfDay) >= MAX_SESSION_MINUTES
}
