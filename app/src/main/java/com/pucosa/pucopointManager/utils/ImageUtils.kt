package com.pucosa.pucopointManager.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.pucosa.pucopointManager.ui.photoPickerBottomSheet.PhotoPickerBottomSheet
import id.zelory.compressor.Compressor
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

data class ChooserIntentData(
    var intent: Intent,
    var file: File
)

object ImageUtils {
    private const val TAG = "ImageUtils"

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    fun getUriForFile(file: File, context: Context): Uri? {
        return FileProvider.getUriForFile(context, "com.pucosauniverse.pucoreads.fileprovider", file)
    }

    fun createCaptureImageIntent(context: Context): ChooserIntentData {
        val file = createImageFile(context)
        val photoURI = getUriForFile(file, context)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        return ChooserIntentData(
            intent = cameraIntent,
            file = file
        )
    }

    fun createGalleryIntent(type: String = "image/*"): Intent {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.action = Intent.ACTION_PICK
        galleryIntent.type = type
        return galleryIntent
    }

    fun createChooserIntentWithBothCameraAndGallery(context: Context, title: String = "Choose a method to get image"): ChooserIntentData {
        val cameraIntentData = createCaptureImageIntent(context)
        val chooserIntent = Intent.createChooser(createGalleryIntent(), title)
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntentData.intent))
        return ChooserIntentData(
            intent = chooserIntent,
            file = cameraIntentData.file
        )
    }

    fun setScaledPic(imageView: ImageView, currentPhotoPath: String) {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    fun handleImageChooserData(intent: Intent?, imageCaptureFile: File?, context: Context, from: String): Uri? {
        var uri: Uri? = null
        Log.d(TAG, "handleImageChooserData: action is : ${intent?.action}; from: $from")
        Log.d(TAG, "handleImageChooserData: is intent null : ${intent == null}, $intent")

        when (from) {
            PhotoPickerBottomSheet.GALLERY_METHOD -> {
                uri = intent?.data
            }
            PhotoPickerBottomSheet.CAMERA_METHOD -> {
                val bitmap = intent?.extras?.get("data") as Bitmap?
                Log.d(TAG, "handleImageChooserData: is bitmap null : ${bitmap == null}; ${imageCaptureFile == null}")
                uri = if(imageCaptureFile != null) {
                    getUriForFile(imageCaptureFile, context)
                } else {
                    null
                }
            }
        }
        Log.i(TAG, "handleImageChooserData: uri is : $uri")

        return uri
    }

    fun compressImage(uri: Uri, fileName: String, context: Context): Uri {
        File.createTempFile(fileName, null, context.cacheDir)
        val currFile = File(context.cacheDir, fileName)
        FileOutputStream(currFile).use { outputStream ->
            context.contentResolver.openInputStream(uri).use { inputStream ->

                inputStream?.copyTo(outputStream)
                outputStream.flush()
            }
        }

        var compressedFile: File
        return try {
            runBlocking {
                compressedFile = Compressor.compress(context, currFile)
            }

            currFile.delete()
            Uri.fromFile(compressedFile)
        } catch (e: Exception) {
            currFile.delete()
            uri
        }

    }
}