package com.android.developer.prof.reda.lifesaver.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.developer.prof.reda.lifesaver.R
import com.android.developer.prof.reda.lifesaver.activities.HomeActivity
import com.android.developer.prof.reda.lifesaver.databinding.FragmentLoginBinding
import com.android.developer.prof.reda.lifesaver.utils.LoginValidation
import com.android.developer.prof.reda.lifesaver.utils.Resource
import com.android.developer.prof.reda.lifesaver.viewModels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val LOGIN_TAG: String = "LoginFragment"
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.loginBtn.setOnClickListener {
            viewModel.login(
                binding.etLoginEmail.text.toString(),
                binding.etLoginPassword.text.toString()
            )
        }

        binding.tvForgetYourPassword.setOnClickListener {

        }

        lifecycleScope.launch {
            viewModel.resetPassword.collectLatest {
                when(it){
                    is Resource.Loading ->{

                    }
                    is Resource.Success ->{
                        Snackbar.make(requireView(), "Reset Link was sent to your email", Snackbar.LENGTH_LONG)
                            .show()
                    }
                    is Resource.Error ->{
                        Snackbar.make(requireView(), "Error: ${it.message.toString()}", Snackbar.LENGTH_LONG)
                            .show()
                        Log.d(LOGIN_TAG, "Error: ${it.message.toString()}")
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.login.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.loginBtn.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.loginBtn.revertAnimation()

                        Intent(requireActivity(), HomeActivity::class.java).also {intent->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error ->{
                        binding.loginBtn.revertAnimation()
                        Log.d(LOGIN_TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }

            viewModel.validation.collectLatest {validation->
                if (validation.email is LoginValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etLoginEmail.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if (validation.password is LoginValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etLoginPassword.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}