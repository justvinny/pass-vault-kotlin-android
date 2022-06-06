package com.vinsonb.password.manager.kotlin.fragments

import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vinsonb.password.manager.kotlin.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateLoginFragmentTest {

    @Test
    fun passcode_textInput_given5DigitsEntered_showsNoError() {
        val scenario = launchFragment<CreateLoginFragment>()
        onView(withId(R.id.passcode))
            .perform(replaceText("55555"))
    }
}