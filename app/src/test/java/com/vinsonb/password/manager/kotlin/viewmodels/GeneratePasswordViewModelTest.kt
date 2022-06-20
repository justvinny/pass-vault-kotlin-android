package com.vinsonb.password.manager.kotlin.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator.DEFAULT_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator.MAX_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator.MIN_LENGTH
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GeneratePasswordViewModelTest {
    private lateinit var viewModelUnderTest: GeneratePasswordViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // Arrange
        viewModelUnderTest = GeneratePasswordViewModel()
    }

    @Test
    fun generatePassword_returnsAppropriateString() {
        viewModelUnderTest.generatePassword(
            hasUppercase = true,
            hasLowercase = true,
            hasNumbers = false,
            hasSpecialCharacters = false
        )
        val expectedStringLength = DEFAULT_LENGTH
        val actualStringLength = viewModelUnderTest.generatedPassword.value!!.length
        Assert.assertEquals(expectedStringLength, actualStringLength)
    }

    @Test
    fun setPasswordLength_givenOverMaxValue_setsToMaxValue() {
        viewModelUnderTest.setPasswordLength(999)
        val expectedValue = MAX_LENGTH
        val actualValue = viewModelUnderTest.passwordLength.value
        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun setPasswordLength_givenLessThanMinValue_setsToMinValue() {
        viewModelUnderTest.setPasswordLength(-999)
        val expectedValue = MIN_LENGTH
        val actualValue = viewModelUnderTest.passwordLength.value
        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun setPasswordLength_givenValidValue_setsToValidValue() {
        viewModelUnderTest.setPasswordLength(60)
        val expectedValue = 60
        val actualValue = viewModelUnderTest.passwordLength.value
        Assert.assertEquals(expectedValue, actualValue)
    }
}