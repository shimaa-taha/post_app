package com.example.post_app.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

fun Uri.toFile(context: Context): File? {
    return try {
        val contentResolver: ContentResolver = context.contentResolver
        val displayName = getFileName(contentResolver, this)

        // Create a temporary file
        val file = File.createTempFile(
            "upload_${System.currentTimeMillis()}",
            displayName?.let { ".${it.substringAfterLast('.', "")}" } ?: ".jpg",
            context.cacheDir
        )

        contentResolver.openInputStream(this)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        file
    } catch (e: Exception) {
        null
    }
}

private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
    return when (uri.scheme) {
        "content" -> {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        cursor.getString(displayNameIndex)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }
        "file" -> uri.lastPathSegment
        else -> null
    }
}