package com.vinsonb.password.manager.kotlin.fragments

import android.content.ClipboardManager
import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.utilities.SliderTestUtilities.setSliderValue
import com.vinsonb.password.manager.kotlin.utilities.SliderTestUtilities.withSliderValue
import com.vinsonb.password.manager.kotlin.utilities.TextViewMatchers.withStringLength
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator.DEFAULT_LENGTH
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeneratePasswordFragmentTest {
    private lateinit var scenario: FragmentScenario<GeneratePasswordFragment>

    @Before
    fun setup() {
        scenario =
            launchFragmentInContainer(themeResId = R.style.Theme_PasswordManagerKotlinRewrite)
    }

    @Test
    fun allViews_withDefaultValues_RenderedCorrectly() {
        onView(withId(R.id.text_generated_password))
            .check(matches(withText("")))

        onView(withId(R.id.image_copy))
            .check(matches(isDisplayed()))

        onView(withId(R.id.input_password_length))
            .check(matches(withText(DEFAULT_LENGTH.toString())))

        onView(withId(R.id.slider_password_length))
            .check(matches(withSliderValue(DEFAULT_LENGTH.toFloat())))

        onView(withId(R.id.switch_uppercase))
            .check(matches(isChecked()))

        onView(withId(R.id.switch_lowercase))
            .check(matches(isChecked()))

        onView(withId(R.id.switch_numbers))
            .check(matches(isNotChecked()))

        onView(withId(R.id.switch_special_symbols))
            .check(matches(isNotChecked()))
    }

    @Test
    fun sliderPasswordLength_givenValue_syncsWith_inputPasswordLength() {
        onView(withId(R.id.slider_password_length))
            .perform(setSliderValue(50f))

        onView(withId(R.id.input_password_length))
            .check(matches(withText("50")))

        onView(withId(R.id.input_password_length))
            .perform(clearText(), clearText(), typeText("60"), closeSoftKeyboard())

        onView(withId(R.id.slider_password_length))
            .check(matches(withSliderValue(60F)))
    }

    @Test
    fun inputPasswordLength_givenOutOfRangeValue_changesValueToValidMax() {
        onView(withId(R.id.input_password_length))
            .perform(typeText("999"), closeSoftKeyboard())
            .check(matches(withText("120")))
    }

    @Test
    fun buttonGeneratePassword_clicked_generatesString() {
        onView(withId(R.id.button_generate_password))
            .perform(click())

        onView(withId(R.id.text_generated_password))
            .check(matches(withStringLength(DEFAULT_LENGTH)))
    }

    @Test
    fun imageCopy_clicked_copiesGeneratePasswordToClipboard() {
        onView(withId(R.id.button_generate_password))
            .perform(click())

        onView(withId(R.id.image_copy))
            .perform(click())

        var clipboardManager: ClipboardManager? = null
        scenario.onFragment {
            clipboardManager =
                it.activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        }

        if (clipboardManager != null) {
            onView(withId(R.id.text_generated_password))
                .check(matches(withText(clipboardManager!!.primaryClip?.getItemAt(0)?.text.toString())))
        }
    }
}
