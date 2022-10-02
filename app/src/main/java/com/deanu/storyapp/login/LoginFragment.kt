package com.deanu.storyapp.login

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.deanu.storyapp.R
import com.deanu.storyapp.common.domain.model.User
import com.deanu.storyapp.common.utils.REQUEST_CODE_PERMISSIONS
import com.deanu.storyapp.common.utils.closeKeyboard
import com.deanu.storyapp.common.utils.isPermissionGranted
import com.deanu.storyapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Checking camera permission
        if (!isPermissionGranted(requireContext(), arrayOf(Manifest.permission.CAMERA))) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMISSIONS
            )
        }

        initListener()
        initViewModelObserver()
        initLoginCheck()
    }

    private fun initLoginCheck() {
        viewModel.isAlreadyLogin.observe(viewLifecycleOwner) { isAlreadyLogin ->
            if (!isAlreadyLogin.isNullOrEmpty()) {
                view?.findNavController()?.navigate(R.id.homeFragment)
            }
        }
    }

    private fun initViewModelObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loading.visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        viewModel.isLoginSuccess.observe(viewLifecycleOwner) { isLoginSuccess ->
            if (isLoginSuccess) {
                view?.findNavController()?.popBackStack()
                view?.findNavController()?.navigate(R.id.homeFragment)
            }
        }

        viewModel.loginResponse.observe(viewLifecycleOwner) { loginResponse ->
            if (loginResponse != null && loginResponse.error == true) {
                Toast.makeText(requireContext(), loginResponse.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initListener() {
        binding.btnSignIn.setOnClickListener {
            closeKeyboard(requireContext(), it)
            if (!binding.edtEmail.isValid() && !binding.edtPassword.isValid()) {
                binding.edtEmail.setError(true, getString(R.string.check_your_email))
                binding.edtPassword.setError(true, getString(R.string.check_password))
            } else if (!binding.edtEmail.isValid()) {
                binding.edtEmail.setError(true, getString(R.string.check_your_email))
            } else if (!binding.edtPassword.isValid()) {
                binding.edtPassword.setError(true, getString(R.string.check_your_email))
            } else {
                val user = User(
                    email = binding.edtEmail.getValue(),
                    password = binding.edtPassword.getValue()
                )
                binding.edtEmail.setError(false, "")
                binding.edtPassword.setError(false, "")
                viewModel.login(user)
            }
        }

        binding.tvRegister.setOnClickListener {
            view?.findNavController()?.navigate(R.id.registerFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        // Reset error state
        binding.edtEmail.setError(false, "")
        binding.edtPassword.setError(false, "")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}