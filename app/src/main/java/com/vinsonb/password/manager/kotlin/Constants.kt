package com.vinsonb.password.manager.kotlin

class Constants {
    companion object {
        object Password {
            const val PASSCODE_MAX_LENGTH = 5

            object SharedPreferenceKeys {
                const val PASSCODE_KEY = "passcode"
                const val SECRET_QUESTION_KEY = "secret question"
                const val SECRET_ANSWER_KEY = "secret answer"
                const val AUTHENTICATED_KEY = "authenticated"
            }
        }
    }
}