package com.vinsonb.password.manager.kotlin

import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator.DEFAULT_LENGTH
import org.junit.Assert.assertEquals
import org.junit.Test

class PasswordGeneratorTest {
    @Test
    fun passwordGenerated_givenNonNegativeLength_returnsPasswordString() {
        val expectedLength = DEFAULT_LENGTH
        val actualLength = PasswordGenerator.createPassword(
            length = DEFAULT_LENGTH,
            hasUppercaseLetters = true,
            hasLowercaseLetters = true,
            hasNumbers = false,
            hasSpecialSymbols = false
        ).length
        assertEquals(expectedLength, actualLength)
    }

    @Test
    fun passwordGenerated_givenNegativeLength_returnsEmptyString() {
        val expectedLength = 0
        val actualLength = PasswordGenerator.createPassword(
            length = -99,
            hasUppercaseLetters = true,
            hasLowercaseLetters = true,
            hasNumbers = false,
            hasSpecialSymbols = false
        ).length
        assertEquals(expectedLength, actualLength)
    }

    @Test
    fun passwordGenerated_givenAllFalseValues_returnsEmptyString() {
        val expectedLength = 0
        val actualLength = PasswordGenerator.createPassword(
            length = DEFAULT_LENGTH,
            hasUppercaseLetters = false,
            hasLowercaseLetters = false,
            hasNumbers = false,
            hasSpecialSymbols = false
        ).length
        assertEquals(expectedLength, actualLength)
    }
}