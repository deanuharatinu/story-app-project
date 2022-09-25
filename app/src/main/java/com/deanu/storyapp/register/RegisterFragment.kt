package com.deanu.storyapp.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.utils.closeKeyboard
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
        initListener()
        initViewModelObserver()
    }

    private fun initViewModelObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loading.visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        viewModel.isUserCreated.observe(viewLifecycleOwner) { isUserCreated ->
            if (isUserCreated) {
                Toast.makeText(requireContext(), "User is created", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.navigateUp()
            }
        }

        viewModel.registerMessage.observe(viewLifecycleOwner) { registerMessage ->
            if (registerMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), registerMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initListener() {
        binding.btnSignUp.setOnClickListener {
            closeKeyboard(requireContext(), it)
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
                binding.edtEmail.setError(false, "")
                binding.edtPassword.setError(false, "")
                viewModel.registerAccount(user)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}