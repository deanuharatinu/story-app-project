package com.deanu.storyapp.common.utils

import android.content.Context
import android.util.TypedValue

// extension function to convert dp to equivalent pixels
// this method return float value
fun Int.dpToPixels(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
)

// extension function to convert dp to equivalent pixels
// this method return integer value
fun Int.dpToPixelsInt(context: Context): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
).toInt()