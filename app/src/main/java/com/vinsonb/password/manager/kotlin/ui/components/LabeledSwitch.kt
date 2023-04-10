package com.vinsonb.password.manager.kotlin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vinsonb.password.manager.kotlin.utilities.ComponentPreviews

@Composable
fun LabeledSwitch(
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@ComponentPreviews
@Composable
private fun PreviewLabeledSwitch() {
    LabeledSwitch(
        label = "Labeled Switch",
        checked = true,
        onCheckedChange = {},
    )
}
