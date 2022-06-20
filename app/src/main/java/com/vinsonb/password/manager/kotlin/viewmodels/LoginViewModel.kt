package com.vinsonb.password.manager.kotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_MAX_LENGTH

class LoginViewModel: ViewModel() {
    private val _password: ArrayDeque<Int> = ArrayDeque()
    private val _passwordLiveData: MutableLiveData<ArrayDeque<Int>> = MutableLiveData(_password)

    /**
     * Adds a digit to the entered passcode.
     */
    fun addDigitToPassword(digit: Int) {
        if (_password.size < PASSCODE_MAX_LENGTH) {
            _password.addLast(digit)
            _passwordLiveData.value = _password
        }
    }

    /**
     * Clears all entered digits for the passcode.
     */
    fun clearAllDigits() {
        _password.clear()
        _passwordLiveData.value = _password
    }

    /**
     * Deletes the last digit entered for the passcode.
     */
    fun clearLastDigit(): Boolean {
        if (_password.removeLastOrNull() != null) {
            _passwordLiveData.value = _password
            return true
        }
        return false
    }

    fun getPassword(): LiveData<ArrayDeque<Int>> {
        return _passwordLiveData
    }
}