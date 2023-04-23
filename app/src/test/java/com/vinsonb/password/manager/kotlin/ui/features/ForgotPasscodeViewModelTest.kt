package com.vinsonb.password.manager.kotlin.ui.features

import app.cash.turbine.test
import com.vinsonb.password.manager.kotlin.runCancellingTest
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeErrors
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeState
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeViewModel
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_MAX_LENGTH
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnitParamsRunner::class)
class ForgotPasscodeViewModelTest {

    /**
     * Test Cases:
     *  1. Test for [ForgotPasscodeErrors.EmptyInputError]
     *  2. Test for [ForgotPasscodeErrors.SecretAnswerMismatchError]
     *  3. Test for [ForgotPasscodeErrors.None]
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
        expectedError: ForgotPasscodeErrors,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()
        viewModel.showDialog()

        // Act
        viewModel.validateSecretAnswer(secretAnswer)

        // Assert
        viewModel.stateFlow.test {
            val actualError = (awaitItem() as ForgotPasscodeState.Visible).secretAnswerErrorState
            assertEquals(expectedError, actualError)
        }
    }

    /**
     * Test Cases:
     *  1. Test for [ForgotPasscodeErrors.EmptyInputError]
     *  2. Test for [ForgotPasscodeErrors.InvalidDigitsError] when < [PASSCODE_MAX_LENGTH]
     *  3. Test for [ForgotPasscodeErrors.InvalidDigitsError] when > [PASSCODE_MAX_LENGTH]
     *  4. Test for [ForgotPasscodeErrors.None]
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
        expectedError: ForgotPasscodeErrors,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()
        viewModel.showDialog()

        // Act
        viewModel.validatePasscode(passcode, repeatPasscode)

        // Assert
        viewModel.stateFlow.test {
            val actualError = (awaitItem() as ForgotPasscodeState.Visible).passcodeErrorState
            assertEquals(expectedError, actualError)
        }
    }

    /**
     * Test Cases:
     *  1. Test for [ForgotPasscodeErrors.EmptyInputError]
     *  2. Test for [ForgotPasscodeErrors.InvalidDigitsError] when < [PASSCODE_MAX_LENGTH]
     *  3. Test for [ForgotPasscodeErrors.InvalidDigitsError] when > [PASSCODE_MAX_LENGTH]
     *  4. Test for [ForgotPasscodeErrors.PasscodeMismatchError]
     *  5. Test for [ForgotPasscodeErrors.None]
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
        expectedError: ForgotPasscodeErrors,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()
        viewModel.showDialog()

        // Act
        viewModel.validateRepeatPasscode(passcode, repeatPasscode)

        // Assert
        viewModel.stateFlow.test {
            val actualError = (awaitItem() as ForgotPasscodeState.Visible).repeatPasscodeErrorState
            assertEquals(expectedError, actualError)
        }
    }

    @Test
    fun `GIVEN saveNewPasscode succeeded WHEN resetPasscode invoked THEN verify state changed to hidden and showSucceededToast invoked`() =
        runCancellingTest {
            // Arrange
            val mockShowSucceededToast: () -> Unit = mock()
            val mockShowFailedToast: () -> Unit = mock()
            val viewModel = provideViewModel(
                showSucceededToast = mockShowSucceededToast,
                showFailedToast = mockShowFailedToast,
            )
            viewModel.showDialog()
            assertEquals(ForgotPasscodeState.Visible(), viewModel.stateFlow.first())

            // Act
            viewModel.resetPasscode("secret")

            // Assert
            viewModel.stateFlow.test {
                assertEquals(ForgotPasscodeState.Hidden, awaitItem())
                verify(mockShowSucceededToast, times(1)).invoke()
                verify(mockShowFailedToast, times(0)).invoke()
            }
        }

    @Test
    fun `GIVEN saveNewPasscode failed WHEN resetPasscode invoked THEN verify showFailedToast invoked`() =
        runCancellingTest {
            // Arrange
            val mockShowSucceededToast: () -> Unit = mock()
            val mockShowFailedToast: () -> Unit = mock()
            val viewModel = provideViewModel(
                saveNewPasscodeReturn = false,
                showSucceededToast = mockShowSucceededToast,
                showFailedToast = mockShowFailedToast,
            )
            viewModel.showDialog()

            // Act
            viewModel.resetPasscode("secret")

            // Assert
            viewModel.stateFlow.test {
                assertEquals(ForgotPasscodeState.Visible(), awaitItem())
                verify(mockShowSucceededToast, times(0)).invoke()
                verify(mockShowFailedToast, times(1)).invoke()
            }
        }

    @Test
    @Parameters(
        value = [
            "asf,false",
            "aasffsafs,false",
            "wrong,false",
            "-=2as,false",
            "123456,false",
            ",true",
            "123,true",
            "12345,true",
        ]
    )
    @TestCaseName("GIVEN {0} as passcode WHEN isValidPasscodeInput invoked THEN return {1}")
    fun `isValidPasscodeInput parameterised test`(
        passcode: String = "",
        expected: Boolean,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        val result = viewModel.isValidPasscodeInput(passcode)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN state hidden WHEN showDialog invoked THEN change state to visible`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel()

            // Act
            viewModel.showDialog()

            // Assert
            viewModel.stateFlow.test {
                assertEquals(ForgotPasscodeState.Visible(), awaitItem())
            }
        }

    @Test
    fun `GIVEN state visible WHEN dismissDialog invoked THEN change state to hidden`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel()
            viewModel.showDialog()
            assertEquals(ForgotPasscodeState.Visible(), viewModel.stateFlow.first())

            // Act
            viewModel.dismissDialog()

            // Assert
            viewModel.stateFlow.test {
                assertEquals(ForgotPasscodeState.Hidden, awaitItem())
            }
        }

    /**
     * Test helpers
     */

    private fun TestScope.provideViewModel(
        saveNewPasscodeReturn: Boolean = true,
        showSucceededToast: () -> Unit = {},
        showFailedToast: () -> Unit = {},
    ) = ForgotPasscodeViewModel(
        scope = this,
        savedSecretAnswer = "secret",
        saveNewPasscode = { saveNewPasscodeReturn },
        showSucceededToast = showSucceededToast,
        showFailedToast = showFailedToast,
    )

    private fun provideArgsValidateSecretAnswer() = arrayOf(
        arrayOf("", ForgotPasscodeErrors.EmptyInputError),
        arrayOf("wrong", ForgotPasscodeErrors.SecretAnswerMismatchError),
        arrayOf("secret", ForgotPasscodeErrors.None),
    )

    private fun provideArgsValidatePasscode() = arrayOf(
        arrayOf("", "", ForgotPasscodeErrors.EmptyInputError),
        arrayOf("123", "", ForgotPasscodeErrors.InvalidDigitsError),
        arrayOf("123456", "", ForgotPasscodeErrors.InvalidDigitsError),
        arrayOf("12345", "12345", ForgotPasscodeErrors.None),
    )

    private fun provideArgsValidateRepeatPasscode() = arrayOf(
        arrayOf("", "", ForgotPasscodeErrors.EmptyInputError),
        arrayOf("", "123", ForgotPasscodeErrors.InvalidDigitsError),
        arrayOf("", "123456", ForgotPasscodeErrors.InvalidDigitsError),
        arrayOf("12345", "12346", ForgotPasscodeErrors.PasscodeMismatchError),
        arrayOf("12345", "12345", ForgotPasscodeErrors.None),
    )
}
