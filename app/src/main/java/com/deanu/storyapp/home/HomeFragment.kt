package com.deanu.storyapp.home

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.deanu.storyapp.R
import com.deanu.storyapp.common.utils.REQUEST_CODE_PERMISSIONS
import com.deanu.storyapp.common.utils.isPermissionGranted
import com.deanu.storyapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
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

        initToolbar()
        initRecyclerView()
        initIsLoadingObserver()
        initStoryList()
        initListener()
    }

    private fun initToolbar() {
        binding.tvToolbarTitle.text = "Home"
    }

    private fun initIsLoadingObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loading.visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun initStoryList() {
        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.getStoryList(token)
            }
        }

        viewModel.storyList.observe(viewLifecycleOwner) { storyList ->
            if (storyList.isNotEmpty()) {
                // TODO: ketika not empty, hide empty component
                adapter.submitList(storyList)
            } else {
                // TODO: ketika empty, show empty component
            }
        }
    }

    private fun initRecyclerView() {
        adapter = StoryAdapter()
        binding.rvStory.adapter = adapter
    }


    private fun initListener() {
        binding.fabAddStory.setOnClickListener {
            view?.findNavController()?.navigate(R.id.addStoryFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}