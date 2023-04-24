package com.vinsonb.password.manager.kotlin.utilities

import com.vinsonb.password.manager.kotlin.extensions.shareIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface EventFlow<T> {
    val eventFlow: SharedFlow<T>

    fun sendEvent(event: T)
}

fun <T> eventFlow(scope: CoroutineScope) = object : EventFlow<T> {
    private val mutableEventFlow = MutableSharedFlow<T>()
    override val eventFlow = mutableEventFlow.asSharedFlow().shareIn(scope)

    override fun sendEvent(event: T) {
        scope.launch{
            mutableEventFlow.emit(event)
        }
    }
}

sealed interface SimpleToastEvent {
    object None : SimpleToastEvent
    object ShowSucceeded : SimpleToastEvent
    object ShowFailed : SimpleToastEvent
}

fun simpleToastEventFlow(scope: CoroutineScope) = object : EventFlow<SimpleToastEvent> {
    private val mutableEventFlow = MutableSharedFlow<SimpleToastEvent>()
    override val eventFlow = mutableEventFlow.asSharedFlow().shareIn(scope)

    override fun sendEvent(event: SimpleToastEvent) {
        scope.launch {
            mutableEventFlow.emit(event)
        }
    }
}
