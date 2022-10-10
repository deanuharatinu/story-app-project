package com.deanu.storyapp.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.deanu.storyapp.R
import com.deanu.storyapp.common.domain.model.Story
import com.deanu.storyapp.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MapsViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStoryList()
        initIsLoadingObserver()
    }

    private fun initStoryList() {
        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.getStoryList(token)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.have_been_logged_out),
                    Toast.LENGTH_SHORT
                ).show()
                view?.findNavController()?.popBackStack()
                view?.findNavController()?.navigate(R.id.loginFragment)
            }
        }

        viewModel.storyList.observe(viewLifecycleOwner) { storyList ->
            if (!storyList.isNullOrEmpty()) {
                initMaps()
            } else {
                Toast.makeText(requireContext(), getString(R.string.data_empty), Toast.LENGTH_SHORT)
                    .show()
            }
        }
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

    private fun initMaps() {
        val mapFragment = binding.map.getFragment() as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        addMarkers(viewModel.getStoryList())
    }

    private fun addMarkers(storyList: List<Story>) {
        val boundsBuilder = LatLngBounds.Builder()

        storyList.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            googleMap?.addMarker(MarkerOptions().position(latLng).title(story.name))
            boundsBuilder.include(latLng)
        }

        val bounds = boundsBuilder.build()
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}