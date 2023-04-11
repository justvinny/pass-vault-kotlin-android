package com.vinsonb.password.manager.kotlin.extensions

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalTime

@RunWith(JUnitParamsRunner::class)
class LocalTimeExtensionsTests {

    /**
     * Test Cases:
     *   1. Minutes goes over the session expired limit when the app is put on the background.(EXPIRED)
     *   2. Minutes is equal to the session expired limit when the app is put on the background.(EXPIRED)
     *   3. Minutes is within the session expired limit when the app is put on the background.(NOT EXPIRED)
     *   4. Minutes is at 0 so no time has passed yet when the app is put on the background. (NOT EXPIRED)
     */
    @Test
    @Parameters(value = [
        "11,true",
        "10,true",
        "9,false",
        "0,false",
    ])
    @TestCaseName("GIVEN {0} minutes elapsed WHEN hasSessionExpired invoked THEN return {1}")
    fun `GIVEN various minutes elapsed input WHEN hasSessionExpired invoked THEN return appropriate boolean`(
        minutesElapsed: Long,
        expected: Boolean,
    ) {
        val oldTime = LocalTime.now().minusMinutes(minutesElapsed).toString()
        assertEquals(expected, LocalTime.now().hasSessionExpired(oldTime))
    }
}
