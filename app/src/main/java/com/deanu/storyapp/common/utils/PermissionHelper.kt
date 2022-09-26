package com.deanu.storyapp.common.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

const val REQUEST_CODE_PERMISSIONS = 10

fun isPermissionGranted(context: Context, permissions: Array<String>) = permissions.all {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}
