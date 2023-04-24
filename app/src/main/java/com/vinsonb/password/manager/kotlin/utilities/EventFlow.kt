package com.vinsonb.password.manager.kotlin.utilities

import com.vinsonb.password.manager.kotlin.extensions.shareIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface EventFlow<T> {
    val eventFlow: SharedFlow<T>

    suspend fun sendEvent(event: T)
}

fun <T> scopedEventFlow(scope: CoroutineScope) = object : EventFlow<T> {
    private val mutableEventFlow = MutableSharedFlow<T>()
    override val eventFlow = mutableEventFlow.asSharedFlow().shareIn(scope)

    override suspend fun sendEvent(event: T) {
        mutableEventFlow.emit(event)
    }
}
