package com.vinsonb.password.manager.kotlin.utilities

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
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

/**
 * Convenient [Preview] Annotation for [Composable] functions which provides both a Light and
 * Dark mode with backgrounds.
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
annotation class ComponentPreviews


/**
 * Convenient [Preview] Annotation for [Composable] Screens which provides light mode, dark mode,
 * the system UI and tablet view.
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true)
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true, showSystemUi = true, device = Devices.PIXEL_C)
@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_C)
annotation class ScreenPreviews
