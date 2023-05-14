package com.vinsonb.password.manager.kotlin.ui.features.viewaccount

import app.cash.turbine.test
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.runCancellingTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotSame
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

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
    fun `onSearch parameterised test`(
        expected: ViewAccountState,
        additionalAccounts: List<Account>?,
    ) = runCancellingTest {
        // Arrange
        val viewModel = provideViewModel(additionalAccounts ?: emptyList())

        // Act
        viewModel.onSearch(expected.searchQuery)

        // Assert
        viewModel.stateFlow.test {
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
                assertEquals(expected, awaitItem().selectedAccount)
            }
        }

    @Test
    fun `GIVEN account updated successfully with a password only change WHEN onUpdateAccount invoked THEN updates stateFlow correctly and verify functions invoked correctly`() =
        runCancellingTest {
            // Arrange
            val mockFunctions = mockFunctions()
            val viewModel = provideViewModel(
                deleteAccount = mockFunctions::deleteAccount,
                insertAccount = mockFunctions::insertAccount,
                updateAccount = mockFunctions::updateAccount,
            )

            viewModel.eventFlow.test {
                // Act
                viewModel.onUpdateAccount(
                    account = Account(
                        platform = "Platform",
                        username = "Username1",
                        password = "NewPassword"
                    ),
                    originalAccount = Account(
                        platform = "Platform",
                        username = "Username1",
                        password = "Password"
                    ),
                )

                // Assert
                assertEquals(ViewAccountToastState.SuccessfullyUpdated, awaitItem())
            }

            verify(mockFunctions, times(0)).deleteAccount(any())
            verify(mockFunctions, times(0)).insertAccount(any())
            verify(mockFunctions, times(1)).updateAccount(any())
        }

    @Test
    fun `GIVEN account updated successfully with a new username and password WHEN onUpdateAccount invoked THEN updates stateFlow correctly and verify functions invoked correctly`() =
        runCancellingTest {
            // Arrange
            val mockFunctions = mockFunctions()
            val viewModel = provideViewModel(
                deleteAccount = mockFunctions::deleteAccount,
                insertAccount = mockFunctions::insertAccount,
                updateAccount = mockFunctions::updateAccount,
            )
            val account = Account(
                platform = "Platform",
                username = "NewUsername",
                password = "NewPassword"
            )

            viewModel.eventFlow.test {
                // Act
                viewModel.onUpdateAccount(
                    account = account,
                    originalAccount = Account(
                        platform = "Platform",
                        username = "Username1",
                        password = "Password"
                    ),
                )

                // Assert
                assertEquals(ViewAccountToastState.SuccessfullyUpdated, awaitItem())
            }

            viewModel.stateFlow.test {
                assertEquals(account, awaitItem().selectedAccount)
            }

            verify(mockFunctions, times(1)).deleteAccount(any())
            verify(mockFunctions, times(1)).insertAccount(any())
            verify(mockFunctions, times(0)).updateAccount(any())
        }

    @Test
    fun `GIVEN account failed to update on a password only change WHEN onUpdateAccount invoked THEN does not update stateFlow and verify functions invoked correctly`() =
        runCancellingTest {
            // Arrange
            val mockFunctions = mockFunctions(
                updateAccountReturn = false,
            )
            val viewModel = provideViewModel(
                deleteAccount = mockFunctions::deleteAccount,
                insertAccount = mockFunctions::insertAccount,
                updateAccount = mockFunctions::updateAccount,
            )

            viewModel.eventFlow.test {
                // Act
                viewModel.onUpdateAccount(
                    account = Account(
                        platform = "Platform",
                        username = "Username1",
                        password = "NewPassword"
                    ),
                    originalAccount = Account(
                        platform = "Platform",
                        username = "Username1",
                        password = "Password"
                    ),
                )

                // Assert
                assertEquals(ViewAccountToastState.FailedAccountUpdate, awaitItem())
            }

            verify(mockFunctions, times(0)).deleteAccount(any())
            verify(mockFunctions, times(0)).insertAccount(any())
            verify(mockFunctions, times(1)).updateAccount(any())
        }

    @Test
    fun `GIVEN account failed to insert new account with new username WHEN onUpdateAccount invoked THEN does not update stateFlow and verify functions invoked correctly`() =
        runCancellingTest {
            // Arrange
            val mockFunctions = mockFunctions(
                insertAccountReturn = false,
            )
            val viewModel = provideViewModel(
                deleteAccount = mockFunctions::deleteAccount,
                insertAccount = mockFunctions::insertAccount,
                updateAccount = mockFunctions::updateAccount,
            )
            val account = Account(
                platform = "Platform",
                username = "NewUsername",
                password = "NewPassword"
            )

            viewModel.eventFlow.test {
                // Act
                viewModel.onUpdateAccount(
                    account = account,
                    originalAccount = Account(
                        platform = "Platform",
                        username = "Username1",
                        password = "Password"
                    ),
                )

                // Assert
                assertEquals(ViewAccountToastState.FailedUsernameUpdate, awaitItem())
            }

            viewModel.stateFlow.test {
                assertNotSame(account, awaitItem().selectedAccount)
            }

            verify(mockFunctions, times(0)).deleteAccount(any())
            verify(mockFunctions, times(1)).insertAccount(any())
            verify(mockFunctions, times(0)).updateAccount(any())
        }

    @Test
    fun `GIVEN new account successfully inserted but failed to delete old account WHEN onUpdateAccount invoked THEN does not update stateFlow, rolls back inserted account and verify functions invoked correctly`() =
        runCancellingTest {
            // Arrange
            val mockFunctions = mockFunctions(
                deleteAccountReturn = false,
            )
            val viewModel = provideViewModel(
                deleteAccount = mockFunctions::deleteAccount,
                insertAccount = mockFunctions::insertAccount,
                updateAccount = mockFunctions::updateAccount,
            )
            val account = Account(
                platform = "Platform",
                username = "NewUsername",
                password = "NewPassword"
            )

            viewModel.eventFlow.test {
                // Act
                viewModel.onUpdateAccount(
                    account = account,
                    originalAccount = Account(
                        platform = "Platform",
                        username = "Username1",
                        password = "Password"
                    ),
                )

                // Assert
                assertEquals(ViewAccountToastState.FailedAccountUpdate, awaitItem())
            }

            viewModel.stateFlow.test {
                assertNotSame(account, awaitItem().selectedAccount)
            }

            verify(mockFunctions, times(2)).deleteAccount(any())
            verify(mockFunctions, times(1)).insertAccount(any())
            verify(mockFunctions, times(0)).updateAccount(any())
        }

    @Test
    fun `GIVEN account deleted successfully WHEN onDeleteAccount invoked THEN updates stateFlow correctly`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel()

            viewModel.eventFlow.test {
                // Act
                viewModel.onDeleteAccount(
                    Account(
                        platform = "NewPlatform",
                        username = "NewUsername",
                        password = "NewPassword"
                    )
                )

                // Assert
                assertEquals(ViewAccountToastState.SuccessfullyDeleted, awaitItem())
            }
        }

    @Test
    fun `GIVEN account failed to delete WHEN onDeleteAccount invoked THEN does not update stateFlow`() =
        runCancellingTest {
            // Arrange
            val viewModel = provideViewModel(updateAccount = { false })

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
            val viewModel = provideViewModel(updateAccount = { false })
            val searchQuery = "updated"
            viewModel.onSearch(searchQuery)

            // Act
            viewModel.onClearSearch()

            // Assert
            viewModel.stateFlow.test {
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

    private interface MockFunctions {
        fun insertAccount(account: Account): Boolean
        fun updateAccount(account: Account): Boolean
        fun deleteAccount(account: Account): Boolean
    }

    private fun mockFunctions(
        deleteAccountReturn: Boolean = true,
        insertAccountReturn: Boolean = true,
        updateAccountReturn: Boolean = true,
    ): MockFunctions {
        return mock {
            on { deleteAccount(any()) }
                .thenReturn(deleteAccountReturn)
            on { insertAccount(any()) }
                .thenReturn(insertAccountReturn)
            on { updateAccount(any()) }
                .thenReturn(updateAccountReturn)
        }
    }

    private fun TestScope.provideViewModel(
        additionalAccounts: List<Account> = emptyList(),
        insertAccount: (Account) -> Boolean = { true },
        updateAccount: (Account) -> Boolean = { true },
        deleteAccount: (Account) -> Boolean = { true },
    ) = ViewAccountViewModel(
        scope = this,
        getAllAccounts = { MutableStateFlow(testAccounts.plus(additionalAccounts)) },
        insertAccount = insertAccount,
        updateAccount = updateAccount,
        deleteAccount = deleteAccount,
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
