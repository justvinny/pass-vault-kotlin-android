package com.vinsonb.password.manager.kotlin.ui.features.saveaccount

import app.cash.turbine.test
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.runCancellingTest
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEvent
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnitParamsRunner::class)
class SaveAccountViewModelTest {

    /**
     * [SaveAccountViewModel.validatePlatform] Test Cases:
     *  1. Test for [SaveAccountError.EmptyInputError]
     *  2. Test for [SaveAccountError.None]
     *
     *  Method source: [provideValidateCommonArgs]
     */
    @Test
    @Parameters(
        method = "provideValidateCommonArgs"
    )
    @TestCaseName("GIVEN {0} as platform WHEN validatePlatform invoked THEN expect {1} as expected error state")
    fun `validatePlatform parameterised test`(
        platform: String,
        expectedError: SaveAccountError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validatePlatform(platform)

        // Assert
        viewModel.stateFlow.test {
            assertEquals(expectedError, awaitItem().platformError)
        }
    }

    /**
     * [SaveAccountViewModel.validateUsername] Test Cases:
     *  1. Test for [SaveAccountError.EmptyInputError]
     *  2. Test for [SaveAccountError.None]
     *
     *  Method source: [provideValidateCommonArgs]
     */
    @Test
    @Parameters(
        method = "provideValidateCommonArgs"
    )
    @TestCaseName("GIVEN {0} as username WHEN validateUsername invoked THEN expect {1} as expected error state")
    fun `validateUsername parameterised test`(
        username: String,
        expectedError: SaveAccountError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validateUsername(username)

        // Assert
        viewModel.stateFlow.test {
            assertEquals(expectedError, awaitItem().usernameError)
        }
    }

    /**
     * [SaveAccountViewModel.validatePassword] Test Cases:
     *  1. Test for [SaveAccountError.EmptyInputError]
     *  2. Test for [SaveAccountError.None]
     *
     *  Method source: [provideValidateCommonArgs]
     */
    @Test
    @Parameters(
        method = "provideValidateCommonArgs"
    )
    @TestCaseName("GIVEN {0} as password WHEN validatePassword invoked THEN expect {1} as expected error state")
    fun `validatePassword parameterised test`(
        password: String,
        expectedError: SaveAccountError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validatePassword(password, password)

        // Assert
        viewModel.stateFlow.test {
            assertEquals(expectedError, awaitItem().passwordError)
        }
    }

    /**
     * [SaveAccountViewModel.validateRepeatPassword] Test Cases:
     *  1. Test for [SaveAccountError.EmptyInputError]
     *  2. Test for [SaveAccountError.PasswordMismatchError]
     *  3. Test for [SaveAccountError.None]
     *
     *  Method source: [provideValidateRepeatPasswordArgs]
     */
    @Test
    @Parameters(
        method = "provideValidateRepeatPasswordArgs"
    )
    @TestCaseName("GIVEN {0} as password and {1} as repeatPassword WHEN validateRepeatPassword invoked THEN expect {2} as expected error state")
    fun `validateRepeatPassword parameterised test`(
        password: String,
        repeatPassword: String,
        expectedError: SaveAccountError,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel()

        // Act
        viewModel.validateRepeatPassword(password, repeatPassword)

        // Assert
        viewModel.stateFlow.test {
            assertEquals(expectedError, awaitItem().repeatPasswordError)
        }
    }

    @Test
    fun `GIVEN account successfully inserted WHEN saveAccount invoked THEN all states should be cleared`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel()
            val mockAccount = provideDummyAccount()
            viewModel.validatePlatform(mockAccount.platform)
            viewModel.validateUsername(mockAccount.username)
            viewModel.validatePassword(mockAccount.password, mockAccount.password)

            viewModel.eventFlow.test {
                // Act
                viewModel.saveAccount(mockAccount)

                // Assert
                assertEquals(SimpleToastEvent.ShowSucceeded, awaitItem())
            }

            // Assert error states also reset
            viewModel.stateFlow.test {
                assertEquals(SaveAccountState(), awaitItem())
            }
        }

    @Test
    fun `GIVEN account failed to insert WHEN saveAccount invoked THEN all states should remain the same`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel(isAccountInserted = false)


            viewModel.eventFlow.test {
                // Act
                viewModel.saveAccount(provideDummyAccount())

                // Assert
                assertEquals(SimpleToastEvent.ShowFailed, awaitItem())
            }
        }

    private fun TestScope.provideViewModel(isAccountInserted: Boolean = true) =
        SaveAccountViewModel(
            this
        ) { isAccountInserted }

    private fun provideDummyAccount() = Account(
        platform = "platform",
        username = "username",
        password = "password",
    )

    private fun provideValidateCommonArgs() = arrayOf(
        arrayOf("", SaveAccountError.EmptyInputError),
        arrayOf("valid", SaveAccountError.None),
    )

    private fun provideValidateRepeatPasswordArgs() = arrayOf(
        arrayOf("", "", SaveAccountError.EmptyInputError),
        arrayOf("incorrect", "wrong", SaveAccountError.PasswordMismatchError),
        arrayOf("correct", "correct", SaveAccountError.None),
    )
}
