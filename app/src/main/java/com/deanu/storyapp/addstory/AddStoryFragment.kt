package com.deanu.storyapp.addstory

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.deanu.storyapp.databinding.FragmentAddStoryBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddStoryViewModel by viewModels()
    private lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initListener()
    }

    private fun initToolbar() {
        // TODO: ganti pake placeholder
        binding.tvToolbarTitle.text = "Add New Story"
    }

    private fun initListener() {
        binding.btnCamera.setOnClickListener {
            startTakePhoto()
        }

        binding.btnGallery.setOnClickListener {
            // TODO: saat klik galery
        }

        binding.btnUpload.setOnClickListener {
            // TODO: saat klik upload, pengecekan apakah ada photonya,
            //       pengecekan, saat ga ada deskripsi, pake placeholder "tidak ada deskripsi" ?
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        com.deanu.storyapp.common.utils.createTempFile(requireActivity().application).also {
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
            val pictureFile = File(currentPhotoPath)
            val result = BitmapFactory.decodeFile(pictureFile.path)
            binding.ivPhotoResult.setImageBitmap(result)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}