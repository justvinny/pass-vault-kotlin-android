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
    fun resetEvent()
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

    override fun resetEvent() {
        scope.launch {
            mutableEventFlow.emit(SimpleToastEvent.None)
        }
    }
}
