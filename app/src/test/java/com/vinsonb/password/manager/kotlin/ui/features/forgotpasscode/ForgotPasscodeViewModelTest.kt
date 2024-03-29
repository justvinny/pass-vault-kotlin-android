package com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode

import app.cash.turbine.test
import com.vinsonb.password.manager.kotlin.runCancellingTest
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_MAX_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEvent
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
class ForgotPasscodeViewModelTest {

    /**
     * Test Cases:
     *  1. Test for [ForgotPasscodeError.EmptyInputError]
     *  2. Test for [ForgotPasscodeError.SecretAnswerMismatchError]
     *  3. Test for [ForgotPasscodeError.None]
     *
     *  Method source: [provideArgsValidateSecretAnswer]
     */
    @Test
    @Parameters(
        method = "provideArgsValidateSecretAnswer"
    )
    @TestCaseName("GIVEN {0} as secret answer WHEN validateSecretAnswer invoked THEN expect {1} as expected error state")
    fun `validateSecretAnswer parameterised test`(
        secretAnswer: String,
        expectedError: ForgotPasscodeError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validateSecretAnswer(secretAnswer)

        // Assert
        viewModel.stateFlow.test {
            assertEquals(expectedError, awaitItem().secretAnswerError)
        }
    }

    /**
     * Test Cases:
     *  1. Test for [ForgotPasscodeError.EmptyInputError]
     *  2. Test for [ForgotPasscodeError.InvalidDigitsError] when < [PASSCODE_MAX_LENGTH]
     *  3. Test for [ForgotPasscodeError.InvalidDigitsError] when > [PASSCODE_MAX_LENGTH]
     *  4. Test for [ForgotPasscodeError.None]
     *
     *  Method source: [provideArgsValidatePasscode]
     */
    @Test
    @Parameters(
        method = "provideArgsValidatePasscode"
    )
    @TestCaseName("GIVEN {0} as passcode and {1} as repeat passcode WHEN validatePasscode invoked THEN expect {2} as expected error state")
    fun `validatePasscode parameterised test`(
        passcode: String,
        repeatPasscode: String,
        expectedError: ForgotPasscodeError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validatePasscode(passcode, repeatPasscode)

        // Assert
        viewModel.stateFlow.test {
            assertEquals(expectedError, awaitItem().passcodeError)
        }
    }

    /**
     * Test Cases:
     *  1. Test for [ForgotPasscodeError.EmptyInputError]
     *  2. Test for [ForgotPasscodeError.InvalidDigitsError] when < [PASSCODE_MAX_LENGTH]
     *  3. Test for [ForgotPasscodeError.InvalidDigitsError] when > [PASSCODE_MAX_LENGTH]
     *  4. Test for [ForgotPasscodeError.PasscodeMismatchError]
     *  5. Test for [ForgotPasscodeError.None]
     *
     *  Method source: [provideArgsValidateRepeatPasscode]
     */
    @Test
    @Parameters(
        method = "provideArgsValidateRepeatPasscode"
    )
    @TestCaseName("GIVEN {0} as passcode and {1} as repeat passcode WHEN validateRepeatPasscode invoked THEN expect {2} as expected error state")
    fun `validateRepeatPasscode parameterised test`(
        passcode: String,
        repeatPasscode: String,
        expectedError: ForgotPasscodeError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validateRepeatPasscode(passcode, repeatPasscode)

        // Assert
        viewModel.stateFlow.test {
            assertEquals(expectedError, awaitItem().repeatPasscodeError)
        }
    }

    @Test
    fun `GIVEN saveNewPasscode succeeded WHEN resetPasscode invoked THEN verify toast event succeeded`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel()

            viewModel.eventFlow.test {
                // Act
                viewModel.resetPasscode("secret")

                // Assert
                assertEquals(SimpleToastEvent.ShowSucceeded, awaitItem())
            }
        }

    @Test
    fun `GIVEN saveNewPasscode failed WHEN resetPasscode invoked THEN verify toast event failed`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel(
                saveNewPasscodeReturn = false,
            )

            viewModel.eventFlow.test {
                // Act
                viewModel.resetPasscode("secret")

                // Assert
                assertEquals(SimpleToastEvent.ShowFailed, awaitItem())
            }
        }

    /**
     * Test helpers
     */

    private fun TestScope.provideViewModel(
        saveNewPasscodeReturn: Boolean = true,
    ) = ForgotPasscodeViewModel(
        scope = this,
        savedSecretAnswer = "secret",
        saveNewPasscode = { saveNewPasscodeReturn },
    )

    private fun provideArgsValidateSecretAnswer() = arrayOf(
        arrayOf("", ForgotPasscodeError.EmptyInputError),
        arrayOf("wrong", ForgotPasscodeError.SecretAnswerMismatchError),
        arrayOf("secret", ForgotPasscodeError.None),
    )

    private fun provideArgsValidatePasscode() = arrayOf(
        arrayOf("", "", ForgotPasscodeError.EmptyInputError),
        arrayOf("123", "", ForgotPasscodeError.InvalidDigitsError),
        arrayOf("123456", "", ForgotPasscodeError.InvalidDigitsError),
        arrayOf("12345", "12345", ForgotPasscodeError.None),
    )

    private fun provideArgsValidateRepeatPasscode() = arrayOf(
        arrayOf("", "", ForgotPasscodeError.EmptyInputError),
        arrayOf("", "123", ForgotPasscodeError.InvalidDigitsError),
        arrayOf("", "123456", ForgotPasscodeError.InvalidDigitsError),
        arrayOf("12345", "12346", ForgotPasscodeError.PasscodeMismatchError),
        arrayOf("12345", "12345", ForgotPasscodeError.None),
    )
}
