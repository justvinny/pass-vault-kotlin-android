package com.vinsonb.password.manager.kotlin.utilities

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme

fun withComposeView(
    context: Context,
    content: @Composable () -> Unit,
): ComposeView = ComposeView(context).apply {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    setContent {
        PassVaultTheme {
            content()
        }
    }
}
