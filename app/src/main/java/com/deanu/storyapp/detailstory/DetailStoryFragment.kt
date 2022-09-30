package com.deanu.storyapp.detailstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.deanu.storyapp.R
import com.deanu.storyapp.databinding.FragmentDetailStoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailStoryFragment : Fragment() {
    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!
    private val args: DetailStoryFragmentArgs by navArgs()
    private lateinit var id: String
    private lateinit var username: String
    private lateinit var photoUrl: String
    private lateinit var description: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(layoutInflater)
        prepareSharedElementTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArgs()
        initToolbar()
        initViews()
    }

    private fun prepareSharedElementTransition() {
        val transition = TransitionInflater.from(binding.root.context)
            .inflateTransition(R.transition.image_shared_element_transition)
        sharedElementEnterTransition = transition

        setEnterSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>,
                    sharedElements: MutableMap<String, View>
                ) {
                    sharedElements[names[0]] = binding.tvUsername
                }
            })
    }

    private fun initToolbar() {
        binding.tvToolbarTitle.text = getString(R.string.story_details)
        binding.toolbar.setNavigationOnClickListener {
            view?.findNavController()?.navigateUp()
        }
    }

    private fun initViews() {
        binding.tvUsername.transitionName = id
        binding.tvUsername.text = username

        Glide.with(binding.root)
            .load(photoUrl)
            .into(binding.ivPhoto)
        binding.tdDescValue.text = description
    }

    private fun initArgs() {
        id = args.id
        username = args.username
        photoUrl = args.photoUrl
        description = args.description
    }
}