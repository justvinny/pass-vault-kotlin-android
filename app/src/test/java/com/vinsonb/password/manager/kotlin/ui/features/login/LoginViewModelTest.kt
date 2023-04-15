package com.vinsonb.password.manager.kotlin.ui.features.login

import app.cash.turbine.test
import com.vinsonb.password.manager.kotlin.runCancellingTest
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnitParamsRunner::class)
class LoginViewModelTest {

    /**
     * Test Cases:
     *  1. Test that it enters 3 digits.
     *  2. Test that it enters 5 digits.
     *  3. Test that it only enters 5 digits given it tried entering more than 5 times.
     */
    @Test
    @Parameters(
        value = [
            "3,012,3",
            "5,01234,5",
            "6,01234,5",
        ]
    )
    @TestCaseName("GIVEN {0} number of digits entered WHEN onEnterPasscodeDigit invoked THEN expect {1} as passcode and {2} as passcodeLength")
    fun `onEnterPasscodeDigit parameterized test`(
        digitsEntered: Int,
        expectedPasscode: String,
        expectedPasscodeLength: Int,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        repeat(digitsEntered) {
            viewModel.onEnterPasscodeDigit(it)
        }

        // Assert
        viewModel.stateFlow.test {
            with(awaitItem()) {
                assertEquals(expectedPasscode, passcode)
                assertEquals(expectedPasscodeLength, passcodeLength)
            }
        }
    }

    /**
     * Test Cases:
     *  1. Test that it leaves 1 digit after clearing the last digit from a 2 length passcode.
     *  2. Test that it leaves 0 digits after clearing the last digit from a 1 length passcode.
     *  3. Test that it leaves 0 digits after clearing the last digit from a 0 length passcode.
     */
    @Test
    @Parameters(
        value = [
            "2,1,0",
            "1,0,",
            "0,0,",
        ]
    )
    @TestCaseName("GIVEN {0} number of digits entered WHEN onClearLastDigit invoked THEN expect {1} as passcodeLength and {2} as passcode")
    fun `onClearLastDigit parameterized test`(
        digitsEntered: Int,
        expectedPasscodeLength: Int,
        expectedPasscode: String = "",
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        repeat(digitsEntered) {
            viewModel.onEnterPasscodeDigit(it)
        }

        // Act
        viewModel.onClearLastDigit()

        // Assert
        viewModel.stateFlow.test {
            with(awaitItem()) {
                assertEquals(expectedPasscodeLength, passcodeLength)
                assertEquals(expectedPasscode, passcode)
            }
        }
    }

    /**
     * Test Cases:
     *  1. Test that it clears all 5 digits.
     *  2. Test that it works even when there are no digits entered.
     */
    @Test
    @Parameters(
        value = [
            "5,0",
            "0,0",
        ]
    )
    @TestCaseName("GIVEN {0} number of digits entered WHEN onClearAllDigits invoked THEN expect {1} as passcodeLength and passcode must be empty")
    fun `onClearALlDigits parameterized test`(
        digitsEntered: Int,
        expectedPasscodeLength: Int,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        repeat(digitsEntered) {
            viewModel.onEnterPasscodeDigit(it)
        }

        // Act
        viewModel.onClearAllDigits()

        // Assert
        viewModel.stateFlow.test {
            with(awaitItem()) {
                assertEquals(expectedPasscodeLength, passcodeLength)
                assertEquals("", passcode)
            }
        }
    }

    private fun TestScope.provideViewModel() = LoginViewModel(this)
}
