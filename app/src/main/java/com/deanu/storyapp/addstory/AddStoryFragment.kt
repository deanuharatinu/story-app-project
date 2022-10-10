package com.deanu.storyapp.addstory

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.Intent.createChooser
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.deanu.storyapp.R
import com.deanu.storyapp.common.utils.REQUEST_CODE_PERMISSIONS
import com.deanu.storyapp.common.utils.createCustomTempFile
import com.deanu.storyapp.common.utils.isPermissionGranted
import com.deanu.storyapp.common.utils.uriToFile
import com.deanu.storyapp.databinding.FragmentAddStoryBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddStoryViewModel by viewModels()
    private lateinit var currentPhotoPath: String
    private lateinit var selectedImage: Uri
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        initLocationListener()
        initToolbar()
        initListener()
        initViewModelObserver()
        animatePhoto()
    }

    private fun initLocationListener() {
        if (!isPermissionGranted(requireContext(), permissions)) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation(description: String, imageFile: File) {
        if (isPermissionGranted(requireContext(), permissions)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d(
                        "location",
                        "getUserLocation: ${location.latitude} and  ${location.longitude}"
                    )
                    viewModel.addNewStory(description, imageFile, location)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun animatePhoto() {
        val objectAnimator = ObjectAnimator.ofFloat(
            binding.ivPhotoResult,
            "alpha",
            0f,
            1f
        )
        objectAnimator.duration = 500
        objectAnimator.start()
    }

    private fun initViewModelObserver() {
        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            if (viewModel.isTakenFromCamera()) {
                val pictureFile = File(currentPhotoPath)
                val result = BitmapFactory.decodeFile(pictureFile.path)
                binding.ivPhotoResult.setImageBitmap(result)
                viewModel.setImageFile(pictureFile)
                animatePhoto()
            } else {
                binding.ivPhotoResult.setImageURI(uri)
                viewModel.setImageFile(uriToFile(uri, requireContext()))
                animatePhoto()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loading.visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        viewModel.addNewStoryResponse.observe(viewLifecycleOwner) { uploadMessage ->
            if (!uploadMessage.error) {
                view?.findNavController()?.navigateUp()
            } else {
                Toast.makeText(requireContext(), uploadMessage.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initToolbar() {
        binding.tvToolbarTitle.text = getString(R.string.add_new_story_title)
        binding.toolbar.setNavigationOnClickListener {
            view?.findNavController()?.navigateUp()
        }
    }

    private fun initListener() {
        binding.btnCamera.setOnClickListener {
            startTakePhoto()
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.btnUpload.setOnClickListener {
            val imageFile = viewModel.getImageFile()
            val description = binding.edtDescription.text.toString()
            if (imageFile != null && description.isNotEmpty()) {
                getUserLocation(description, imageFile)
            } else if (imageFile == null) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.image_not_found),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (description.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.description_cannot_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImage = result.data?.data as Uri
            viewModel.setImageUri(selectedImage)
            viewModel.setIsTakenFromCamera(false)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        createCustomTempFile(requireActivity().application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity(),
                requireActivity().packageName + ".provider",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            viewModel.setImageUri(currentPhotoPath.toUri())
            viewModel.setIsTakenFromCamera(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}