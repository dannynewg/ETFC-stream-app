package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object LocalImageStorageManager {

    private const val TAG = "LocalImageStorageManager"
    private const val UPLOADS_DIR = "etfc_media_uploads"

    /**
     * Copies an image from a content Uri (e.g. from Photo Picker or File Chooser)
     * to persistent app-internal storage so that it remains accessible offline,
     * survives app restarts, and can be rendered by Coil via file:// URI.
     */
    suspend fun saveImageFromUri(
        context: Context,
        sourceUri: Uri,
        prefix: String = "img"
    ): String = withContext(Dispatchers.IO) {
        try {
            val mediaDir = File(context.filesDir, UPLOADS_DIR).apply {
                if (!exists()) mkdirs()
            }

            val fileName = "${prefix}_${System.currentTimeMillis()}.jpg"
            val destinationFile = File(mediaDir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                // Decode bounds first to see if compression is needed
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                if (originalBitmap != null) {
                    FileOutputStream(destinationFile).use { outStream ->
                        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream)
                    }
                } else {
                    // Fallback raw copy if decode fails
                    context.contentResolver.openInputStream(sourceUri)?.use { rawIn ->
                        FileOutputStream(destinationFile).use { rawOut ->
                            rawIn.copyTo(rawOut)
                        }
                    }
                }
            }

            val fileUriString = "file://${destinationFile.absolutePath}"
            Log.d(TAG, "Successfully saved picked image to: $fileUriString")
            fileUriString
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy picked image to local storage", e)
            sourceUri.toString()
        }
    }

    /**
     * Deletes a previously stored local upload if needed.
     */
    suspend fun deleteLocalImage(fileUriString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (fileUriString.startsWith("file://")) {
                val filePath = fileUriString.removePrefix("file://")
                val file = File(filePath)
                if (file.exists()) {
                    return@withContext file.delete()
                }
            }
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting local image: $fileUriString", e)
            false
        }
    }
}
