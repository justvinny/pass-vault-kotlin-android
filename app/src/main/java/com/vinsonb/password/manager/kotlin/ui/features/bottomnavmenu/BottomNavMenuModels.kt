package com.vinsonb.password.manager.kotlin.ui.features.bottomnavmenu

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.vinsonb.password.manager.kotlin.ui.features.navigation.NavigationDestination

data class BottomNavMenuItem(
    val icon: ImageVector,
    @StringRes val labelRes: Int,
    val navDestination: NavigationDestination,
)
