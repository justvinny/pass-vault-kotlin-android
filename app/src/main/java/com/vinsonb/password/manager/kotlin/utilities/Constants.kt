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

    object Credits {
        val CREDITS = listOf(
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

    object MimeType {
        const val CSV = "text/comma-separated-values"
    }

    object FileName {
        const val DEFAULT_FILENAME = "filename"
    }

    object Csv {
        const val DELIMITER = ",_____,"
    }

    object Toast {
        const val SHORT_DELAY = 2000L
    }
}
