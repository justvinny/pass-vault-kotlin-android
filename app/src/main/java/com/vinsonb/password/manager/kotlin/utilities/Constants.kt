package com.vinsonb.password.manager.kotlin.utilities

object Constants {
    object Password {
        const val PASSCODE_MAX_LENGTH = 5
        const val PASSCODE_REGEX_PATTERN = "\\d{1,$PASSCODE_MAX_LENGTH}"

        object SharedPreferenceKeys {
            const val PASSCODE_KEY = "passcode"
            const val SECRET_QUESTION_KEY = "secret question"
            const val SECRET_ANSWER_KEY = "secret answer"
            const val AUTHENTICATED_KEY = "authenticated"
        }
    }

    object Database {
        const val DATABASE_NAME = "pass-vault"
    }

    object MimeType {
        const val CSV = "text/comma-separated-values"
    }

    object Csv {
        const val DELIMITER = ",_____,"
    }

    object Toast {
        const val SHORT_DELAY = 2000L
    }
}
