package com.vob.unlocktracker.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun BitmapToByteArray(bitmap: Bitmap): ByteArray
    {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun ByteArrayToBitmap(byteArray: ByteArray) =
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)


}