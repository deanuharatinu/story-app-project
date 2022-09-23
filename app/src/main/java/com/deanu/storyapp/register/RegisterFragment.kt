package com.deanu.storyapp.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            if (!binding.edtEmail.isValid() && !binding.edtPassword.isValid()) {
                binding.edtEmail.setError(true, "Email must be valid and not empty")
                binding.edtPassword.setError(true, "Password length must more than 6 characters")
            } else if (!binding.edtEmail.isValid()) {
                binding.edtEmail.setError(true, "Email must be valid and not empty")
            } else if (!binding.edtPassword.isValid()) {
                binding.edtPassword.setError(true, "Password length must more than 6 characters")
            } else {
                // Register valid
                val user = User(
                    binding.edtUsername.getValue(),
                    binding.edtEmail.getValue(),
                    binding.edtPassword.getValue()
                )

                viewModel.registerAccount(user)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = RegisterFragment()
    }

}