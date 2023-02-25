package com.example.passvault.ui.utilities

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

fun Modifier.adaptiveScreenPaddings(): Modifier = composed {
    when {
        LocalConfiguration.current.screenWidthDp > 840 -> padding(
            vertical = 16.dp,
            horizontal = 300.dp,
        )
        else -> padding(16.dp)
    }
}



