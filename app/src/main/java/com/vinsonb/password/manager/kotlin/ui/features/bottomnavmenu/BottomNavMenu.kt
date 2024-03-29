package com.vinsonb.password.manager.kotlin.ui.features.bottomnavmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.features.navigation.NavigationDestination
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ComponentPreviews

private val MENU_ITEMS = arrayOf(
    BottomNavMenuItem(
        icon = Icons.Filled.ManageAccounts,
        labelRes = R.string.menu_item_view,
        navDestination = NavigationDestination.VIEW_ACCOUNTS,
    ),
    BottomNavMenuItem(
        icon = Icons.Filled.Save,
        labelRes = R.string.menu_item_save,
        navDestination = NavigationDestination.SAVE_ACCOUNT,
    ),
    BottomNavMenuItem(
        icon = Icons.Filled.Factory,
        labelRes = R.string.menu_item_generate_pass,
        navDestination = NavigationDestination.GENERATE_PASSWORD,
    ),
)

@Composable
fun BottomNavMenu(navigateTo: (NavigationDestination) -> Unit) {
    BottomAppBar(
        contentPadding = PaddingValues(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
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
            }
        }
    }
}

@Composable
private fun textButtonColors(selectedIndex: Int, index: Int) = if (selectedIndex == index) {
    ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.inversePrimary)
} else {
    ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
}

@ComponentPreviews
@Composable
private fun PreviewBottomNavMenu() = PassVaultTheme {
    BottomNavMenu(navigateTo = {})
}
