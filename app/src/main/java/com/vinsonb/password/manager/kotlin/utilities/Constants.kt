package com.vinsonb.password.manager.kotlin.utilities

import com.vinsonb.password.manager.kotlin.models.Credit

object Constants {
    object Password {
        const val PASSCODE_MAX_LENGTH = 5

        object SharedPreferenceKeys {
            const val PASSCODE_KEY = "passcode"
            const val SECRET_QUESTION_KEY = "secret question"
            const val SECRET_ANSWER_KEY = "secret answer"
            const val AUTHENTICATED_KEY = "authenticated"
        }

        object Timer {
            const val MAX_TIMER_MILLI = 600000L
            const val TIMER_INTERVAL_MILLI = 1000L
        }
    }

    object Database {
        const val DATABASE_NAME = "pass-vault"
    }

    // TODO Add page for credits.
    object Credits {
        val CREDITS = listOf(
            Credit(
                title = "Expand icon created by mavadee - Flaticon",
                url = "https://www.flaticon.com/free-icons/expand"
            ),
            Credit(
                title = "Platform icon created by Freepik - Flaticon",
                url = "https://www.flaticon.com/free-icons/platform"
            ),
            Credit(
                title = "Secure Login created by Katerina Limpitsouni - Undraw",
                url = "https://undraw.co/"
            )
        )
    }
}
