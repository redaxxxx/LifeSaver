package com.android.developer.prof.reda.lifesaver.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.developer.prof.reda.lifesaver.R
import com.android.developer.prof.reda.lifesaver.data.User
import com.android.developer.prof.reda.lifesaver.databinding.FragmentRegisterBinding
import com.android.developer.prof.reda.lifesaver.utils.RegisterValidation
import com.android.developer.prof.reda.lifesaver.utils.Resource
import com.android.developer.prof.reda.lifesaver.viewModels.RegistrationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TAG: String = "RegisterFragment"
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegistrationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_register,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.registerBtn.setOnClickListener{
            binding.apply {
                viewModel.createAccountWithEmailAndPassword(
                    User(
                        etRegisterFirstName.text.toString().trim(),
                        etRegisterLastName.text.toString().trim(),
                        etRegisterEmail.text.toString().trim()
                    ),
                    etRegisterPassword.text.toString().trim()
                )
            }
        }
        lifecycleScope.launch{

            viewModel.register.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.registerBtn.startAnimation()
                    }
                    is Resource.Success ->{
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        binding.registerBtn.revertAnimation()
                    }
                    is Resource.Error ->{
                        Log.d(TAG, "Error: ${it.message.toString()}")
                        binding.registerBtn.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.validation.collectLatest {validation->
                if(validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etRegisterEmail.apply{
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if(validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etRegisterPassword.apply{
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
                if(validation.firstName is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etRegisterFirstName.apply{
                            requestFocus()
                            error = validation.firstName.message
                        }
                    }
                }
                if(validation.lastName is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etRegisterLastName.apply{
                            requestFocus()
                            error = validation.lastName.message
                        }
                    }
                }
            }
        }
    }
}