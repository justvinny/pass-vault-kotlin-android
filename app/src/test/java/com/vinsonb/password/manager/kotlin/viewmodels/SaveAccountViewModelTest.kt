package com.vinsonb.password.manager.kotlin.viewmodels

import app.cash.turbine.test
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountState
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountState.TextFieldName.*
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountState.TextFieldState.ErrorState.*
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountViewModel
import junit.framework.TestCase.assertEquals
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnitParamsRunner::class)
class SaveAccountViewModelTest {

    /**
     * Test Case Scenarios:
     *  1. Test for valid non-empty input.
     *  2. Test for invalid empty input.
     *  3. Test for invalid input passwords don't match.
     *  4. Test for valid input passwords match.
     *
     *  Method Params Provider: [provideValidateParams]
     */
    @Test
    @Parameters(method = "provideValidateParams")
    fun `GIVEN various cases WHEN validate invoked THEN errorState should change appropriately`(
        textFieldName: SaveAccountState.TextFieldName,
        text1: String,
        text2: String?,
        expectedErrorState: SaveAccountState.TextFieldState.ErrorState,
    ) = runTest {
        // Arrange
        val viewModel = provideViewModel()

        viewModel.onTextChange(textFieldName, text1)
        if (textFieldName == REPEAT_PASSWORD) {
            viewModel.onTextChange(PASSWORD, text2!!)
        }

        // Act
        viewModel.validate(textFieldName)

        // Assert
        viewModel.stateFlow.test {
            assertEquals(expectedErrorState, awaitItem().textFields[textFieldName]?.errorState)
        }
    }

    @Test
    fun `GIVEN all text is empty WHEN validateAll invoked THEN all error states should be TEXT_EMPTY`() =
        runTest {
            // Arrange
            val viewModel = provideViewModel()

            // Act
            viewModel.validateAll()

            // Assert
            viewModel.stateFlow.test {
                awaitItem().textFields.values.forEach {
                    assertEquals(TEXT_EMPTY, it.errorState)
                }
            }
        }

    @Test
    fun `Given new text WHEN onTextChange invoked THEN update text value of state`() = runTest {
        // Arrange
        val viewModel = provideViewModel()
        val expected = "new value"

        // Act
        viewModel.onTextChange(PLATFORM, expected)

        // Assert
        viewModel.stateFlow.test {
            assertEquals(expected, awaitItem().textFields[PLATFORM]?.text)
        }
    }

    @Test
    fun `GIVEN account successfully inserted WHEN saveAccount invoked THEN all states should be cleared`() =
        runTest {
            // Arrange
            val viewModel = provideViewModel()
            viewModel.setDummyAccount()

            // Act
            viewModel.saveAccount()

            // Assert
            viewModel.stateFlow.test {
                awaitItem().textFields.values
                    .all { it.text.isBlank() }
                    .also { isAllBlank ->
                        assert(isAllBlank)
                    }
            }
        }

    @Test
    fun `GIVEN account failed to insert WHEN saveAccount invoked THEN all states should remain the same`() =
        runTest {
            // Arrange
            val viewModel = provideViewModel(isAccountInserted = false)
            val dummyAccount = viewModel.setDummyAccount()

            // Act
            viewModel.saveAccount()

            // Assert
            viewModel.stateFlow.test {
                awaitItem().textFields.forEach {
                    assertEquals(
                        /* expected = */
                        when (it.key) {
                            PLATFORM -> dummyAccount.platform
                            USERNAME -> dummyAccount.username
                            PASSWORD, REPEAT_PASSWORD -> dummyAccount.password
                        },
                        /* actual = */
                        it.value.text,
                    )
                }
            }
        }

    private fun provideViewModel(isAccountInserted: Boolean = true) =
        SaveAccountViewModel { isAccountInserted }

    private fun SaveAccountViewModel.setDummyAccount(): Account {
        val account = Account(
            platform = "Platform",
            username = "Username",
            password = "Password",
        )

        onTextChange(PLATFORM, account.platform)
        onTextChange(USERNAME, account.username)
        onTextChange(PASSWORD, account.password)
        onTextChange(REPEAT_PASSWORD, account.password)
        validateAll()

        return account
    }

    private fun provideValidateParams() = arrayOf(
        arrayOf(PLATFORM, "good input", null, NO_ERROR),
        arrayOf(REPEAT_PASSWORD, "password", "password", NO_ERROR),
        arrayOf(USERNAME, "", null, TEXT_EMPTY),
        arrayOf(REPEAT_PASSWORD, "password", "pass", PASSWORDS_MUST_MATCH),
    )
}
