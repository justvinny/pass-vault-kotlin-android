package com.vinsonb.password.manager.kotlin.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator.DEFAULT_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator.MAX_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator.MIN_LENGTH

class GeneratePasswordViewModel : ViewModel() {
    val generatedPassword = MutableLiveData("")
    val passwordLength = MutableLiveData(DEFAULT_LENGTH)

    /**
     * Generates a new password using the [PasswordGenerator.createPassword] and sets that new
     * password to [generatedPassword]
     */
    fun generatePassword(
        hasUppercase: Boolean,
        hasLowercase: Boolean,
        hasNumbers: Boolean,
        hasSpecialCharacters: Boolean
    ) {
        val password = PasswordGenerator.createPassword(
            passwordLength.value?.toInt()!!,
            hasUppercase,
            hasLowercase,
            hasNumbers,
            hasSpecialCharacters
        )
        generatedPassword.value = password
    }

    /**
     * Setter method for [passwordLength] that guarantees that the value does not go below the
     * [MIN_LENGTH] or go above the [MAX_LENGTH].
     */
    fun setPasswordLength(passwordLength: Int) {
        if (passwordLength < MIN_LENGTH) this.passwordLength.value = MIN_LENGTH
        else if (passwordLength > MAX_LENGTH) this.passwordLength.value = MAX_LENGTH
        else this.passwordLength.value = passwordLength
    }
}
