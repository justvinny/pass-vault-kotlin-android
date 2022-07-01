package com.vinsonb.password.manager.kotlin.utilities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.vinsonb.password.manager.kotlin.R

object ClipboardUtilities {
    const val CLIP_USERNAME_LABEL = "Account Username"
    const val CLIP_PASSWORD_LABEL = "Account Password"

    /**
     * Copies text to the clipboard and notify user when successful.
     */
    fun copyToClipboard(context: Context, clipLabel: String, toCopy: String, message: String = " ") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(clipLabel, toCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            context,
            context.getString(R.string.success_copied_to_clipboard, message),
            Toast.LENGTH_SHORT
        ).show()
    }
}