package com.vinsonb.password.manager.kotlin.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

private const val SUBSCRIPTION_DURATION = 5000L

fun <T> Flow<T>.stateIn(scope: CoroutineScope, initialValue: T) = this.stateIn(
    scope = scope,
    started = WhileSubscribed(SUBSCRIPTION_DURATION),
    initialValue = initialValue,
)

fun <T> Flow<T>.shareIn(scope: CoroutineScope) = this.shareIn(
    scope = scope,
    started = WhileSubscribed(SUBSCRIPTION_DURATION),
)
