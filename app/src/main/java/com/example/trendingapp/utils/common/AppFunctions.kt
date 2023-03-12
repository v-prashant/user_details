package com.example.trendingapp.utils.common

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.*

object AppFunctions {

    fun getBitmap(photoByteArray: ByteArray?): Bitmap? {
        photoByteArray?.let { return BitmapFactory.decodeByteArray(it, 0, it.size) }
        return null
    }

    fun getBytesFromBitmap(bitmap: Bitmap?): ByteArray? {
        if(bitmap == null){
            return null
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

}