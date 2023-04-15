package com.vinsonb.password.manager.kotlin.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpacer(height: Int = 16) {
    Spacer(modifier = Modifier.height(height.dp))
}

@Composable
fun ColumnScope.VerticalFillSpacer(weight: Float = 1f) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun RowScope.HorizontalFillSpacer(weight: Float = 1f) {
    Spacer(modifier = Modifier.weight(weight))
}
