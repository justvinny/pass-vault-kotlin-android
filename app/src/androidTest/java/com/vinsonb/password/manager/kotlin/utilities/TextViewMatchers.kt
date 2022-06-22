package com.vinsonb.password.manager.kotlin.utilities

import android.view.View
import android.widget.TextView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object TextViewMatchers {
    fun withStringLength(expectedLength: Int): Matcher<View> = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("expected length: $expectedLength")
        }

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextView) return false
            return item.text.length == expectedLength
        }
    }
}