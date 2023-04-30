package com.vinsonb.password.manager.kotlin.ui.features.createlogin

import app.cash.turbine.test
import com.vinsonb.password.manager.kotlin.runCancellingTest
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_MAX_LENGTH
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnitParamsRunner::class)
class CreateLoginViewModelTest {

    /**
     * Test Cases:
     *  1. Test for [CreateLoginError.EmptyInputError]
     *  2. Test for [CreateLoginError.InvalidDigitsError] when passcode length > [PASSCODE_MAX_LENGTH]
     *  3. Test for [CreateLoginError.InvalidDigitsError] when passcode length < [PASSCODE_MAX_LENGTH]
     *  4. Test for [CreateLoginError.None]
     *
     *  Method source: [provideValidatePasscodeArgs]
     */
    @Test
    @Parameters(
        method = "provideValidatePasscodeArgs"
    )
    @TestCaseName("GIVEN {0} as passcode WHEN validatePasscode invoked THEN expect {1} as expected error state")
    fun `validatePasscode parameterised test`(
        passcode: String,
        expectedError: CreateLoginError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validatePasscode(passcode, passcode)

        // Assert
        viewModel.stateFlow.test {
            Assert.assertEquals(expectedError, awaitItem().passcodeError)
        }
    }

    /**
     * Test Cases:
     *  1. Test for [CreateLoginError.EmptyInputError]
     *  2. Test for [CreateLoginError.InvalidDigitsError] when repeatPasscode length > [PASSCODE_MAX_LENGTH]
     *  3. Test for [CreateLoginError.InvalidDigitsError] when repeatPasscode length < [PASSCODE_MAX_LENGTH]
     *  4. Test for [CreateLoginError.PasscodeMismatchError]
     *  5. Test for [CreateLoginError.None]
     *
     *  Method source: [provideValidateRepeatPasscodeArgs]
     */
    @Test
    @Parameters(
        method = "provideValidateRepeatPasscodeArgs"
    )
    @TestCaseName("GIVEN {0} as passcode and {1} as repeat passcode WHEN validateRepeatPasscode invoked THEN expect {2} as expected error state")
    fun `validateRepeatPasscode parameterised test`(
        passcode: String,
        repeatPasscode: String,
        expectedError: CreateLoginError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validateRepeatPasscode(passcode, repeatPasscode)

        // Assert
        viewModel.stateFlow.test {
            Assert.assertEquals(expectedError, awaitItem().repeatPasscodeError)
        }
    }

    /**
     * Test Cases:
     *  1. Test for [CreateLoginError.EmptyInputError]
     *  2. Test for [CreateLoginError.None]
     *
     *  Method source: [provideArgsValidateCommonArgs]
     */
    @Test
    @Parameters(
        method = "provideArgsValidateCommonArgs"
    )
    @TestCaseName("GIVEN {0} as secret answer WHEN validateSecretAnswer invoked THEN expect {1} as expected error state")
    fun `validateSecretAnswer parameterised test`(
        secretAnswer: String,
        expectedError: CreateLoginError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validateSecretAnswer(secretAnswer)

        // Assert
        viewModel.stateFlow.test {
            Assert.assertEquals(expectedError, awaitItem().secretAnswerError)
        }
    }

    /**
     * Test Cases:
     *  1. Test for [CreateLoginError.EmptyInputError]
     *  2. Test for [CreateLoginError.None]
     *
     *  Method source: [provideArgsValidateCommonArgs]
     */
    @Test
    @Parameters(
        method = "provideArgsValidateCommonArgs"
    )
    @TestCaseName("GIVEN {0} as secret question WHEN validateSecretQuestion invoked THEN expect {1} as expected error state")
    fun `validateSecretQuestion parameterised test`(
        secretQuestion: String,
        expectedError: CreateLoginError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validateSecretQuestion(secretQuestion)

        // Assert
        viewModel.stateFlow.test {
            Assert.assertEquals(expectedError, awaitItem().secretQuestionError)
        }
    }

    private fun TestScope.provideViewModel() = CreateLoginViewModel(
        scope = this,
        _createLogin = { _, _, _ -> }
    )

    private fun provideArgsValidateCommonArgs() = arrayOf(
        arrayOf("", CreateLoginError.EmptyInputError),
        arrayOf("valid", CreateLoginError.None),
    )

    private fun provideValidatePasscodeArgs() = arrayOf(
        arrayOf("", CreateLoginError.EmptyInputError),
        arrayOf("123456", CreateLoginError.InvalidDigitsError),
        arrayOf("123", CreateLoginError.InvalidDigitsError),
        arrayOf("12345", CreateLoginError.None),
    )

    private fun provideValidateRepeatPasscodeArgs() = arrayOf(
        arrayOf("", "", CreateLoginError.EmptyInputError),
        arrayOf("123456", "123456", CreateLoginError.InvalidDigitsError),
        arrayOf("123", "123", CreateLoginError.InvalidDigitsError),
        arrayOf("12345", "22222", CreateLoginError.PasscodeMismatchError),
        arrayOf("12345", "12345", CreateLoginError.None),
    )
}
