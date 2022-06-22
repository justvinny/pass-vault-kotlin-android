package com.vinsonb.password.manager.kotlin.utilities

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.google.android.material.slider.Slider
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object SliderTestUtilities {
    fun withSliderValue(expectedValue: Float): Matcher<View> = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("expected: $expectedValue")
        }

        override fun matchesSafely(item: View?): Boolean {
            if (item !is Slider) return false
            return item.value == expectedValue
        }
    }

    fun setSliderValue(value: Float): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(Slider::class.java)
        }

        override fun getDescription(): String {
            return "Sets the slider value to $value"
        }

        override fun perform(uiController: UiController?, view: View?) {
            if (view is Slider) {
                view.value = value
            }
        }
    }
}