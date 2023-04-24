package com.vinsonb.password.manager.kotlin.utilities

import androidx.compose.runtime.mutableStateOf
import org.junit.Assert.assertEquals
import org.junit.Test

class MutableStateUtilitiesTest {

    @Test
    fun `GIVEN non-empty mutable state string WHEN clearMutableStateStrings invoked THEN mutate state to have empty string`() {
        // Arrange
        val expected = ""
        val actual = mutableStateOf("delete this string")

        // Act
        clearMutableStateStrings(actual)

        // Assert
        assertEquals(expected, actual.value)
    }

    @Test
    fun `GIVEN multiple non-empty mutable state strings WHEN clearMutableStateStrings invoked THEN mutate all states to have empty strings`() {
        // Arrange
        val expected = ""
        val actual = arrayOf(
            mutableStateOf("delete this string"),
            mutableStateOf("another one to be deleted"),
            mutableStateOf("this will also be cleared"),
        )

        // Act
        clearMutableStateStrings(*actual)

        // Assert
        actual.forEach { eachActual ->
            assertEquals(expected, eachActual.value)
        }
    }
}