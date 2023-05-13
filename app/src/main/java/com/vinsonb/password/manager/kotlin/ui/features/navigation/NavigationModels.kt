package com.vinsonb.password.manager.kotlin.ui.features.navigation

enum class NavigationDestination(val destination: String) {
    CREATE_LOGIN("create_login"),
    LOGIN("login"),
    VIEW_ACCOUNTS("view_accounts"),
    SAVE_ACCOUNT("save_account"),
    GENERATE_PASSWORD("generate_password");
}
