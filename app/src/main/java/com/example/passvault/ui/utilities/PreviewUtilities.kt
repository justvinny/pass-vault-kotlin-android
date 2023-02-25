package com.example.passvault.ui.utilities

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.passvault.ui.theme.PassVaultTheme

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview(device = "spec:width=1280dp,height=800dp,dpi=480")
@Preview(device = "spec:width=1280dp,height=800dp,dpi=480", uiMode = UI_MODE_NIGHT_YES)
annotation class ScreenPreviews

object PreviewUtilities {

    @Composable
    fun Screen(content: @Composable () -> Unit) {
        PassVaultTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                content()
            }
        }
    }
}