package com.vinsonb.password.manager.kotlin.ui.features.credits

data class Credit(
    val title: String,
    val url: String,
)

val CREDITS_DATA = listOf(
    Credit(
        title = "Platform icon created by Freepik - Flaticon",
        url = "https://www.flaticon.com/free-icons/platform",
    ),
    Credit(
        title = "Secure Login created by Katerina Limpitsouni - Undraw",
        url = "https://undraw.co/",
    )
)
