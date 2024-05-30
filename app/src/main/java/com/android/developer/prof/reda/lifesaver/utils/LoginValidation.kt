package com.android.developer.prof.reda.lifesaver.utils

import android.os.Message
import java.util.Objects

sealed class LoginValidation(){
    data object Success: RegisterValidation()
    data class Failed(val message: String) : RegisterValidation()
}
data class LoginFailedState(
    val email: RegisterValidation,
    val password: RegisterValidation
)