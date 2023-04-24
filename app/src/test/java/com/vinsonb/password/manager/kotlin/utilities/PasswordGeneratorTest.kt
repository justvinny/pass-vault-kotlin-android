package com.vinsonb.password.manager.kotlin.utilities

import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator.DEFAULT_LENGTH
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class PasswordGeneratorTest {
    @Test
    fun passwordGenerated_givenNonNegativeLength_returnsPasswordString() {
        // Arrange
        val expectedLength = DEFAULT_LENGTH

        // Act
        val actualLength = PasswordGenerator.createPassword(
            length = DEFAULT_LENGTH,
            hasUppercaseLetters = true,
            hasLowercaseLetters = true,
            hasNumbers = false,
            hasSpecialSymbols = false
        ).length

        // Assert
        assertEquals(expectedLength, actualLength)
    }

    @Test
    fun passwordGenerated_givenNegativeLength_returnsEmptyString() {
        // Arrange
        val expectedLength = 0

        // Act
        val actualLength = PasswordGenerator.createPassword(
            length = -99,
            hasUppercaseLetters = true,
            hasLowercaseLetters = true,
            hasNumbers = false,
            hasSpecialSymbols = false
        ).length

        // Assert
        assertEquals(expectedLength, actualLength)
    }

    @Test
    fun passwordGenerated_givenAllFalseValues_returnsEmptyString() {
        // Arrange
        val expectedLength = 0

        // Act
        val actualLength = PasswordGenerator.createPassword(
            length = DEFAULT_LENGTH,
            hasUppercaseLetters = false,
            hasLowercaseLetters = false,
            hasNumbers = false,
            hasSpecialSymbols = false
        ).length

        // Assert
        assertEquals(expectedLength, actualLength)
    }

    /**
     * Test Cases:
     *   1. Tests that passwordLength below [PasswordGenerator.MIN_LENGTH] returns [PasswordGenerator.MIN_LENGTH]
     *   2. Tests that passwordLength above [PasswordGenerator.MAX_LENGTH] returns [PasswordGenerator.MAX_LENGTH]
     *   3. Tests that valid [passwordLength] returns that [passwordLength]
     */
    @Test
    @Parameters(
        value = [
            "-1,${PasswordGenerator.MIN_LENGTH}",
            "121,${PasswordGenerator.MAX_LENGTH}",
            "75,75",
        ],
    )
    @TestCaseName("GIVEN {0} password length WHEN getValidPasswordLength invoked THEN return valid length {1}")
    fun `getValidPasswordLength parameterised test`(
        passwordLength: Int,
        expectedLength: Int,
    ) {
        // Act
        val actualLength = PasswordGenerator.getValidPasswordLength(passwordLength)

        // Assert
        assertEquals(expectedLength, actualLength)
    }
}
