package com.pucosa.pucopointManager.utils

import android.content.Context
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class TempImageFiles {

    private val CAMERA_IMAGE_FILE_NAME = ""

    private fun createEmptyImageTempFile(context: Context): File? {
        val f = File(context.filesDir, CAMERA_IMAGE_FILE_NAME)
        f.delete()
        var fos: FileOutputStream? = null
        try {
            fos = context.openFileOutput(
                CAMERA_IMAGE_FILE_NAME,
                Context.MODE_WORLD_WRITEABLE
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return getImageTempFile(context)
    }

    private fun getImageTempFile(context: Context): File {
        return File(context.filesDir, "image.tmp")
    }
}