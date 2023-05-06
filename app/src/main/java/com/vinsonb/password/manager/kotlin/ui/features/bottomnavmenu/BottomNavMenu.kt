package com.vinsonb.password.manager.kotlin.ui.features.bottomnavmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.components.HorizontalFillSpacer
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ComponentPreviews

private val MENU_ITEMS = arrayOf(
    BottomNavMenuItem(
        icon = Icons.Filled.ManageAccounts,
        labelRes = R.string.menu_item_view,
        navDestination = R.id.view_accounts_fragment,
    ),
    BottomNavMenuItem(
        icon = Icons.Filled.Save,
        labelRes = R.string.menu_item_save,
        navDestination = R.id.save_account_fragment,
    ),
    BottomNavMenuItem(
        icon = Icons.Filled.Factory,
        labelRes = R.string.menu_item_generate_pass,
        navDestination = R.id.generate_password_fragment,
    ),
)

@Composable
fun BottomNavMenu(navigateTo: (Int) -> Unit) {
    BottomAppBar {
        HorizontalFillSpacer()

        var selectedIndex by rememberSaveable { mutableStateOf(0) }

        MENU_ITEMS.forEachIndexed { index, item ->
            TextButton(
                onClick = {
                    selectedIndex = index
                    navigateTo(item.navDestination)
                },
                colors = textButtonColors(selectedIndex = selectedIndex, index = index),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(imageVector = item.icon, contentDescription = null)
                    Text(
                        text = stringResource(id = item.labelRes),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            HorizontalFillSpacer()
        }
    }
}

@Composable
private fun textButtonColors(selectedIndex: Int, index: Int) = if (selectedIndex == index) {
    ButtonDefaults.textButtonColors()
} else {
    ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
}

@ComponentPreviews
@Composable
private fun PreviewBottomNavMenu() = PassVaultTheme {
    BottomNavMenu(navigateTo = {})
}
