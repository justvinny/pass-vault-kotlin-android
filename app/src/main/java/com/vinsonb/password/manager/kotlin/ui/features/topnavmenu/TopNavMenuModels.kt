package com.vinsonb.password.manager.kotlin.ui.features.topnavmenu

import androidx.annotation.StringRes

data class TopNavMenuItem(
    @StringRes val itemNameRes: Int,
    val action: () -> Unit,
)
