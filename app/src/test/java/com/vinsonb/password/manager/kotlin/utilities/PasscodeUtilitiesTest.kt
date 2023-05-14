package com.vinsonb.password.manager.kotlin.utilities

import com.vinsonb.password.manager.kotlin.runCancellingTest
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnitParamsRunner::class)
class PasscodeUtilitiesTest {

    @Test
    @Parameters(
        value = [
            "asf,false",
            "aasffsafs,false",
            "wrong,false",
            "-=2as,false",
            "123456,false",
            ",false",
            "123,true",
            "12345,true",
        ]
    )
    @TestCaseName("GIVEN {0} as passcode WHEN isValidPasscodeInput invoked THEN return {1}")
    fun `isValidPasscodeInput parameterised test`(
        passcode: String = "",
        expected: Boolean,
    ) = runCancellingTest {
        // Act
        val result = isValidPasscodeInput(passcode)

        // Assert
        Assert.assertEquals(expected, result)
    }
}
