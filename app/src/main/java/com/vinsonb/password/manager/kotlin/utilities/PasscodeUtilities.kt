package com.vinsonb.password.manager.kotlin.utilities


fun isValidPasscodeInput(passcode: String): Boolean {
    return passcode.matches(Regex(Constants.Password.PASSCODE_REGEX_PATTERN))
}
