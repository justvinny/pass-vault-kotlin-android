package com.vinsonb.password.manager.kotlin

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

/**
 * Convenience test function when dealing with hot StateFlow where the collecting coroutine needs to
 * be cancelled manually at the end of the test.
 */
@ExperimentalCoroutinesApi
fun runCancellingTest(content: suspend TestScope.() -> Unit) = runTest {
    content()
    coroutineContext.cancelChildren()
}
