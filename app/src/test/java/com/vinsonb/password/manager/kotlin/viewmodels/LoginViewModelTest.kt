package com.vinsonb.password.manager.kotlin.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModelUnderTest: LoginViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // Arrange
        viewModelUnderTest = LoginViewModel()
    }

    @Test
    fun addDigitToPassword_addsOneNumber() {
        viewModelUnderTest.addDigitToPassword(5)
        val expectedSize = 1
        val actualSize = viewModelUnderTest.getPassword().value?.size
        assertEquals(expectedSize, actualSize)
    }

    @Test
    fun addDigitToPassword_doesNotAddWhenFull() {
        for (i in 0..6) {
            viewModelUnderTest.addDigitToPassword(i)
        }

        val expectedSize = 5
        val actualSize = viewModelUnderTest.getPassword().value?.size
        assertEquals(expectedSize, actualSize)
    }

    @Test
    fun clearAllDigits() {
        for (i in 0..6) {
            viewModelUnderTest.addDigitToPassword(i)
        }
        viewModelUnderTest.clearAllDigits()

        val expectedSize = 0
        val actualSize = viewModelUnderTest.getPassword().value?.size
        assertEquals(expectedSize, actualSize)
    }

    @Test
    fun clearLastDigit() {
        for (i in 0..4) {
            viewModelUnderTest.addDigitToPassword(i)
        }
        val expectedLastDigit = 3
        val isDigitCleared = viewModelUnderTest.clearLastDigit()
        val actualSize = viewModelUnderTest.getPassword().value?.last()
        assert(isDigitCleared)
        assertEquals(expectedLastDigit, actualSize)
    }
}