package com.android.developer.prof.reda.lifesaver.utils

import android.util.Patterns
import java.util.regex.Pattern

fun validateRegisterFirstName(firstName: String): RegisterValidation{
    if (firstName.isEmpty()){
        return RegisterValidation.Failed("First name cannot be empty")
    }
    return RegisterValidation.Success
}

fun validateRegisterLastName(lastName: String): RegisterValidation{
    if (lastName.isEmpty()){
        return RegisterValidation.Failed("Last name cannot be empty")
    }

    return RegisterValidation.Success
}

fun validateRegisterEmail(email: String): RegisterValidation{
    if (email.isEmpty()){
        return RegisterValidation.Failed("Email cannot be empty")
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        return RegisterValidation.Failed("Wrong email format")
    }
    return RegisterValidation.Success
}

fun validateRegisterPassword(password: String): RegisterValidation{
    if (password.isEmpty()){
        return RegisterValidation.Failed("Password cannot be empty")
    }
    if (password.length < 11){
        return RegisterValidation.Failed("Password should contain 11 char")
    }
    return RegisterValidation.Success
}