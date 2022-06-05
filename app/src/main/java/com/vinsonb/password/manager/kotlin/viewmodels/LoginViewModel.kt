package com.vinsonb.password.manager.kotlin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val PASSWORD_MAX = 5
class LoginViewModel: ViewModel() {
    private val _password: ArrayDeque<Int> = ArrayDeque()
    private val _passwordLiveData: MutableLiveData<ArrayDeque<Int>> = MutableLiveData(_password)

    fun addDigitToPassword(digit: Int) {
        if (_password.size < PASSWORD_MAX) {
            _password.addLast(digit)
            _passwordLiveData.value = _password
        }
    }

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