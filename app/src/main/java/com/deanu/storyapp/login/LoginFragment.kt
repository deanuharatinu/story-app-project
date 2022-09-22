package com.deanu.storyapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.deanu.storyapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginViewModel
    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            if (!binding.edtEmail.isValid()) {
//                binding.edtEmail.setError(true, "Email must be valid and not empty")
                binding.edtEmail.setError(true, "Please check your email")
            } else {
                binding.edtEmail.setError(false, "")
            }

            if (!binding.edtPassword.isValid()) {
//                binding.edtPassword.setError(true, "Password length must more than 6 characters")
                binding.edtPassword.setError(true, "Please check your password")
            } else {
                binding.edtPassword.setError(false, "")
            }


        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

    companion object {
        fun newInstance() = LoginFragment()
    }


}