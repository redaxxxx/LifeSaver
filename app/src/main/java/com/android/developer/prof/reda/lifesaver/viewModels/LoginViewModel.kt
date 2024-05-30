package com.android.developer.prof.reda.lifesaver.viewModels

import androidx.lifecycle.ViewModel
import com.android.developer.prof.reda.lifesaver.utils.LoginFailedState
import com.android.developer.prof.reda.lifesaver.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
): ViewModel(){
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login: SharedFlow<Resource<FirebaseUser>>
        get() = _login

    private val _validation = Channel<LoginFailedState>()
    val validation = _validation.receiveAsFlow()
}