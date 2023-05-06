package com.vinsonb.password.manager.kotlin.ui.features.bottomnavmenu

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavMenuItem(
    val icon: ImageVector,
    @StringRes val labelRes: Int,
    @IdRes val navDestination: Int,
)
