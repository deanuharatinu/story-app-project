package com.deanu.storyapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.deanu.storyapp.R
import com.deanu.storyapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                binding.edtEmail.setError(true, "Please check your email")
            } else {
                binding.edtEmail.setError(false, "")
            }

            if (!binding.edtPassword.isValid()) {
                binding.edtPassword.setError(true, "Please check your password")
            } else {
                binding.edtPassword.setError(false, "")
            }
        }

        binding.tvRegister.setOnClickListener {
            view.findNavController().navigate(R.id.registerFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        // Reset error state
        binding.edtEmail.setError(false, "")
        binding.edtPassword.setError(false, "")
    }

    companion object {
        fun newInstance() = LoginFragment()
    }


}