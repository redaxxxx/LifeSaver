package com.android.developer.prof.reda.lifesaver.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.developer.prof.reda.lifesaver.data.User
import com.android.developer.prof.reda.lifesaver.utils.LoginFailedState
import com.android.developer.prof.reda.lifesaver.utils.RegisterValidation
import com.android.developer.prof.reda.lifesaver.utils.Resource
import com.android.developer.prof.reda.lifesaver.utils.validateLoginEmail
import com.android.developer.prof.reda.lifesaver.utils.validateLoginPassword
import com.android.developer.prof.reda.lifesaver.utils.validateRegisterEmail
import com.android.developer.prof.reda.lifesaver.utils.validateRegisterFirstName
import com.android.developer.prof.reda.lifesaver.utils.validateRegisterLastName
import com.android.developer.prof.reda.lifesaver.utils.validateRegisterPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
): ViewModel(){
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login: SharedFlow<Resource<FirebaseUser>>
        get() = _login

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword: SharedFlow<Resource<String>>
        get() = _resetPassword

    private val _validation = Channel<LoginFailedState>()
    val validation = _validation.receiveAsFlow()

    fun login(email: String, password: String){
        if (checkValidation(email, password)){
            runBlocking {
                _login.emit(Resource.Loading())
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {result->
                    viewModelScope.launch {
                        result.user?.let {
                            _login.emit(Resource.Success(it))
                        }
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _login.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    private fun resetPassword(email: String){
        runBlocking {
            _resetPassword.emit(Resource.Loading())
        }

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun checkValidation(email: String, password: String): Boolean {
        val emailValidation = validateLoginEmail(email)
        val passwordValidation = validateLoginPassword(password)

        return emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success
    }
}