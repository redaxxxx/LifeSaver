package com.android.developer.prof.reda.lifesaver.utils

import android.os.Message
import java.util.Objects

sealed class RegisterValidation(){
    data object Success: RegisterValidation()
    data class Failed(val message: String) : RegisterValidation()
}
data class RegistrationFailedState(
    val firstName: RegisterValidation,
    val lastName: RegisterValidation,
    val email: RegisterValidation,
    val password: RegisterValidation
)