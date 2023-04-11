package com.vinsonb.password.manager.kotlin.ui.features.viewaccount

import app.cash.turbine.test
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.runCancellingTest
import junit.framework.TestCase.assertEquals
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnitParamsRunner::class)
class ViewAccountViewModelTest {

    /**
     * Test Case Scenarios:
     *  1. Tests that it filters nothing when search query is blank.
     *  2. Tests that it correctly filters accounts by platform / username when search query is valid.
     *  3. Tests that it does not show filter results for matching passwords.
     *  4. Tests that the filter result is an empty list when search query does not exist.
     *
     *  Method Params Provider: [provideOnSearchArgs]
     */
    @Test
    @Parameters(method = "provideOnSearchArgs")
    fun `GIVEN valid queries WHEN onSearch invoked THEN filters accounts appropriately`(
        expected: ViewAccountState,
        additionalAccounts: List<Account>?,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel(additionalAccounts ?: emptyList())

        // Act
        viewModel.onSearch(expected.searchQuery)

        // Assert
        viewModel.stateFlow.test {
            awaitItem()
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN account WHEN onSelectAccount invoked THEN updates stateFlow correctly`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel()
            val expected = testAccounts[0]

            // Act
            viewModel.onSelectAccount(expected)

            // Assert
            viewModel.stateFlow.test {
                awaitItem()
                assertEquals(expected, awaitItem().selectedAccount)
            }
        }

    @Test
    fun `GIVEN account added successfully WHEN onUpdateAccount invoked THEN updates stateFlow correctly`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel()

            // Act
            viewModel.onUpdateAccount(
                Account(
                    platform = "NewPlatform",
                    username = "NewUsername",
                    password = "NewPassword"
                )
            )

            // Assert
            viewModel.stateFlow.test {
                awaitItem()
                assertEquals(ViewAccountToastState.SuccessfullyUpdated, awaitItem().toastState)
                assertEquals(ViewAccountToastState.Idle, awaitItem().toastState)
            }
        }

    @Test
    fun `GIVEN account failed to add WHEN onUpdateAccount invoked THEN does not update stateFlow`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel(updateAccountReturn = false)

            // Act
            viewModel.onUpdateAccount(
                Account(
                    platform = "NewPlatform",
                    username = "NewUsername",
                    password = "NewPassword"
                )
            )

            // Assert
            viewModel.stateFlow.test {
                awaitItem()
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN account deleted successfully WHEN onDeleteAccount invoked THEN updates stateFlow correctly`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel()

            // Act
            viewModel.onDeleteAccount(
                Account(
                    platform = "NewPlatform",
                    username = "NewUsername",
                    password = "NewPassword"
                )
            )

            // Assert
            viewModel.stateFlow.test {
                awaitItem()
                assertEquals(ViewAccountToastState.SuccessfullyDeleted, awaitItem().toastState)
                assertEquals(ViewAccountToastState.Idle, awaitItem().toastState)
            }
        }

    @Test
    fun `GIVEN account failed to delete WHEN onDeleteAccount invoked THEN does not update stateFlow`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel(updateAccountReturn = false)

            // Act
            viewModel.onDeleteAccount(
                Account(
                    platform = "NewPlatform",
                    username = "NewUsername",
                    password = "NewPassword"
                )
            )

            // Assert
            viewModel.stateFlow.test {
                awaitItem()
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN searchQuery not empty WHEN onClearSearch invoked THEN updates stateFlow to have empty searchQuery`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel(updateAccountReturn = false)
            val searchQuery = "updated"
            viewModel.onSearch(searchQuery)

            // Act
            viewModel.onClearSearch()

            // Assert
            viewModel.stateFlow.test {
                awaitItem()
                assertEquals("", awaitItem().searchQuery)
            }
        }

    private val testAccounts = listOf(
        Account(
            platform = "Platform",
            username = "Username1",
            password = "Password",
        ),
        Account(
            platform = "Platform1",
            username = "Username1",
            password = "Password1",
        ),
        Account(
            platform = "Platform2",
            username = "Username2",
            password = "Password2",
        ),
    )

    private fun TestScope.provideViewModel(
        additionalAccounts: List<Account> = emptyList(),
        updateAccountReturn: Boolean = true,
        deleteAccountReturn: Boolean = true,
    ) = ViewAccountViewModel(
        scope = this,
        getAllAccounts = { MutableStateFlow(testAccounts.plus(additionalAccounts)) },
        updateAccount = { updateAccountReturn },
        deleteAccount = { deleteAccountReturn },
    )

    private fun provideOnSearchArgs() = arrayOf(
        arrayOf(ViewAccountState(accounts = testAccounts), null),
        arrayOf(
            ViewAccountState(
                accounts = listOf(
                    Account(
                        platform = "Platform",
                        username = "Username1",
                        password = "Password",
                    ),
                    Account(
                        platform = "Platform1",
                        username = "Username1",
                        password = "Password1",
                    ),
                ),
                searchQuery = "username1"
            ),
            null,
        ),
        arrayOf(
            ViewAccountState(searchQuery = "passwordmustnotmatch"),
            listOf(
                Account(
                    platform = "Platform",
                    username = "badpassword",
                    password = "passwordmustnotmatch"
                ),
            ),
        ),
        arrayOf(ViewAccountState(searchQuery = "no accounts found"), null),
    )
}
