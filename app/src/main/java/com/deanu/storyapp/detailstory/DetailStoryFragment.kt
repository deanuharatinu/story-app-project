package com.deanu.storyapp.detailstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.deanu.storyapp.R
import com.deanu.storyapp.databinding.FragmentDetailStoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailStoryFragment : Fragment() {
    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!
    private val args: DetailStoryFragmentArgs by navArgs()
    private lateinit var username: String
    private lateinit var photoUrl: String
    private lateinit var description: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArgs()
        initToolbar()
        iniViews()
    }

    private fun initToolbar() {
        binding.tvToolbarTitle.text = getString(R.string.story_details)
        binding.toolbar.setNavigationOnClickListener {
            view?.findNavController()?.navigateUp()
        }
    }

    private fun iniViews() {
        binding.tvUsername.text = username
        Glide.with(binding.root)
            .load(photoUrl)
            .into(binding.ivPhoto)
        binding.tdDescValue.text = description
    }

    private fun initArgs() {
        username = args.username
        photoUrl = args.photoUrl
        description = args.description
    }
}