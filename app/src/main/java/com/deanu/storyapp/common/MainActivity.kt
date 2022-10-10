package com.deanu.storyapp.common

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.deanu.storyapp.R
import com.deanu.storyapp.common.utils.REQUEST_CODE_PERMISSIONS
import com.deanu.storyapp.common.utils.isPermissionGranted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPermissions()
    }

    private fun initPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (!isPermissionGranted(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }
}