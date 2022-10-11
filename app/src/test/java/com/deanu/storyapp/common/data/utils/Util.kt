package com.deanu.storyapp.common.data.utils

import android.content.Context
import androidx.paging.PagingSource
import com.deanu.storyapp.common.utils.createCustomTempFile
import org.mockito.Mockito
import java.io.*

fun getImageAsset(filename: String, context: Context): File {
    try {
        val fileOut = createCustomTempFile(context)

        val inputStream: InputStream = context.assets.open("image/$filename")
        val outputStream: OutputStream = FileOutputStream(fileOut)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return fileOut
    } catch (exception: IOException) {
        throw exception
    }
}

object MockitoHelper {
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    @Suppress("UNCHECKED_CAST")
    fun <T> uninitialized(): T =  null as T
}