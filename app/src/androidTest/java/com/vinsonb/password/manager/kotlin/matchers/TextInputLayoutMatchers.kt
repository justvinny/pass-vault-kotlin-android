package com.vinsonb.password.manager.kotlin.matchers

import android.view.View
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class TextInputLayoutMatchers {
    companion object {
        fun withTextInputLayoutErrorText(expectedErrorText: String): Matcher<View> =
            object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description?) {}

                override fun matchesSafely(item: View?): Boolean {
                    if (item !is TextInputLayout) return false
                    val error = item.error ?: return false
                    val hint = error.toString()
                    println("$hint $expectedErrorText")
                    return expectedErrorText == hint
                }
            }

        fun containsTextInputLayoutErrorText(expectedErrorText: String): Matcher<View> =
            object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description?) {}

                override fun matchesSafely(item: View?): Boolean {
                    if (item !is TextInputLayout) return false
                    val error = item.error.toString()
                    return error.contains(expectedErrorText)
                }
            }

        fun withTextInputLayoutIsErrorEnabled(expectedIsErrorEnabled: Boolean): Matcher<View> =
            object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description?) {}

                override fun matchesSafely(item: View?): Boolean {
                    if (item !is TextInputLayout) return false
                    val errorEnabled = item.isErrorEnabled
                    return expectedIsErrorEnabled == errorEnabled
                }
            }
    }
}