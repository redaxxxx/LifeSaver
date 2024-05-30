package com.android.developer.prof.reda.lifesaver.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.developer.prof.reda.lifesaver.data.User
import com.android.developer.prof.reda.lifesaver.utils.RegisterValidation
import com.android.developer.prof.reda.lifesaver.utils.RegistrationFailedState
import com.android.developer.prof.reda.lifesaver.utils.Resource
import com.android.developer.prof.reda.lifesaver.utils.User_COLLECTION
import com.android.developer.prof.reda.lifesaver.utils.validateRegisterEmail
import com.android.developer.prof.reda.lifesaver.utils.validateRegisterFirstName
import com.android.developer.prof.reda.lifesaver.utils.validateRegisterLastName
import com.android.developer.prof.reda.lifesaver.utils.validateRegisterPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: StateFlow<Resource<User>>
        get() = _register

    private val _validation = Channel<RegistrationFailedState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String){
        if (checkValidation(user, password)){
            runBlocking {
                 _register.emit(Resource.Loading())
            }

            auth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {result->
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            result.user?.let {
                                saveUserInfo(it.uid, user)
                            }
                        }
                        ?.addOnFailureListener {
                            viewModelScope.launch {
                                _register.emit(Resource.Error(it.message.toString()))
                            }
                        }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _register.emit(Resource.Error(it.message.toString()))
                    }
                }
        }else{
            val registerFailedState = RegistrationFailedState(
                validateRegisterFirstName(user.firstName),
                validateRegisterLastName(user.lastName),
                validateRegisterEmail(user.email),
                validateRegisterPassword(password)
            )

            runBlocking {
                _validation.send(registerFailedState)
            }
        }
    }

    private fun saveUserInfo(uid: String, user: User) {
        db.collection(User_COLLECTION).document(uid)
            .set(user)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _register.emit(Resource.Success(user))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _register.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val firstNameValidation = validateRegisterFirstName(user.firstName)
        val lastNameValidation = validateRegisterLastName(user.lastName)
        val emailValidation = validateRegisterEmail(user.email)
        val passwordValidation = validateRegisterPassword(password)

        return firstNameValidation is RegisterValidation.Success &&
                lastNameValidation is RegisterValidation.Success &&
                emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success
    }
}