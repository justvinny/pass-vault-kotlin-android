package com.vinsonb.password.manager.kotlin.utilities

import androidx.compose.runtime.MutableState

fun clearMutableStateStrings(vararg stateStrings: MutableState<String>) {
    for (stateString in stateStrings) {
        stateString.value = ""
    }
}
