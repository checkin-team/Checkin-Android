package com.checkin.app.checkin.auth.exceptions

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class InvalidOTPException(msg: String = "Invalid OTP entered. Try again with the correct one.") : FirebaseAuthInvalidCredentialsException(msg, msg)