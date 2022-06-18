package com.vinsonb.password.manager.kotlin.utilities

import kotlin.random.Random

object PasswordGenerator {
    const val MIN_LENGTH = 0
    const val MAX_LENGTH = 120
    const val DEFAULT_LENGTH = 20
    private val UPPERCASE_LETTERS = 'A'..'Z'
    private val LOWERCASE_LETTERS = 'a'..'z'
    private val NUMBERS = '0'..'9'
    private val SPECIAL_SYMBOLS = '!'..'/'

    /**
     * Creates a randomly generated password with a length given by a user.
     *
     * @param length - length of the password to be generated.
     * @param hasUppercaseLetters - whether or not to include uppercase letters.
     * @param hasLowercaseLetters - whether or not to include lowercase letters.
     * @param hasNumbers - whether or not to include numbers.
     * @param hasSpecialSymbols - whether or not to include special symbols.
     * @return a randomly generated password string based on the arguments given.
     */
    fun createPassword(
        length: Int,
        hasUppercaseLetters: Boolean,
        hasLowercaseLetters: Boolean,
        hasNumbers: Boolean,
        hasSpecialSymbols: Boolean
    ): String {
        if (!(hasUppercaseLetters || hasLowercaseLetters || hasNumbers || hasSpecialSymbols)) return ""

        val includedCharacters = mutableListOf<Char>()
        if (hasUppercaseLetters) includedCharacters.addAll(UPPERCASE_LETTERS)
        if (hasLowercaseLetters) includedCharacters.addAll(LOWERCASE_LETTERS)
        if (hasNumbers) includedCharacters.addAll(NUMBERS)
        if (hasSpecialSymbols) includedCharacters.addAll(SPECIAL_SYMBOLS)

        val password = StringBuilder()
        repeat(length) {
            password.append(includedCharacters[Random.nextInt(0, includedCharacters.size)])
        }

        return password.toString()
    }
}