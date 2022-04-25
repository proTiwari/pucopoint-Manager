package com.pucosa.pucopointManager.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.pucosa.pucopointManager.ui.photoPickerBottomSheet.PhotoOptionClickListeners
import com.pucosa.pucopointManager.ui.photoPickerBottomSheet.PhotoPickerBottomSheet
import java.io.File

private interface CallbackListeners{
    fun onCancelled()
    fun onImageNotFound()
    fun onSuccess(uri: Uri?, from: ImageCaptureMethod)
}

class ImageCaptureManager(frag: Fragment, val onImageGot: (uri: Uri?, from: ImageCaptureMethod, uniqueRequestCode: Int) -> Unit) {

    private var fragment: Fragment = frag
    private var galleryActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var imageCaptureFile: File? = null
    private var uniqueRequestCode: Int = 10001
    private var callbackListener: CallbackListeners = object : CallbackListeners {
        override fun onCancelled() {
            Log.d(
                TAG,
                "onActivityResult: image picker request cancelled."
            )
        }

        override fun onImageNotFound() {
            Log.d(
                TAG,
                "onActivityResult: image data is null"
            )
            showImageFetchErrorToast()
        }

        override fun onSuccess(uri: Uri?, from: ImageCaptureMethod) {
            onImageGot(uri, from, uniqueRequestCode)
        }

    }

    init {
        initResultLaunchers(frag)
    }

    fun startImageChooser(uniqueRequestCode: Int = 1001, removeRequired: Boolean = false, forProfile: Boolean = false, cropAspect: IntArray = intArrayOf(10, 16)) {
        try {
            this.uniqueRequestCode = uniqueRequestCode
            PhotoPickerBottomSheet(object : PhotoOptionClickListeners {
                override fun onGallerySelected(photoPickerBottomSheet: PhotoPickerBottomSheet) {
                    val picker = ImagePicker.with(fragment)
                        .galleryMimeTypes(  //Exclude gif images
                            mimeTypes = arrayOf(
                                "image/png",
                                "image/jpg",
                                "image/jpeg"
                            )
                        )
                        .galleryOnly()

                    val (x, y) = cropAspect
                    if (forProfile) picker.cropSquare()
                    else picker.crop(x.toFloat(), y.toFloat())

                    picker.createIntent {
                        galleryActivityResultLauncher!!.launch(it)
                    }

                    photoPickerBottomSheet.dismiss()
                }

                override fun onCameraSelected(photoPickerBottomSheet: PhotoPickerBottomSheet) {
                    imageCaptureFile = fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val picker = ImagePicker.with(fragment)
                        .cameraOnly()
                        .saveDir(imageCaptureFile!!)

                    if (forProfile) picker.cropSquare()
                    else picker.crop(10f, 16f)	//Crop image with 10:16 aspect ratio

                    picker.createIntent {
                        cameraActivityResultLauncher!!.launch(it)
                    }
                    photoPickerBottomSheet.dismiss()
                }

                override fun onRemoveSelected(photoPickerBottomSheet: PhotoPickerBottomSheet) {
                    super.onRemoveSelected(photoPickerBottomSheet)
                    callbackListener.onSuccess(null, ImageCaptureMethod.REMOVE)
                    photoPickerBottomSheet.dismiss()

                }

            }, removeRequired).show(fragment.childFragmentManager, TAG)

        } catch (e: Exception) {
            Toast.makeText(
                fragment.requireContext().applicationContext,
                "Sorry! Unable to open image picker.",
                Toast.LENGTH_SHORT
            ).show()
            Log.e(TAG, "openImagePicker: error found", e)
        }
    }

    private fun initResultLaunchers(frag: Fragment) {
        galleryActivityResultLauncher = frag.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            galleryCallback()
        )
        cameraActivityResultLauncher = frag.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            cameraCallback()
        )
    }

    private fun galleryCallback(): ActivityResultCallback<ActivityResult> {
        return ActivityResultCallback {
            if (it.resultCode == Activity.RESULT_CANCELED) {
                callbackListener.onCancelled()
            } else if (it.resultCode == Activity.RESULT_OK) {
                val uri: Uri? =
                    ImageUtils.handleImageChooserData(it.data, imageCaptureFile, fragment.requireContext(), PhotoPickerBottomSheet.GALLERY_METHOD)

                if (uri == null) {
                    // error getting image
                    callbackListener.onImageNotFound()
                    return@ActivityResultCallback

                }
                // got image uri
                callbackListener.onSuccess(uri, ImageCaptureMethod.GALLERY)
            }
        }
    }

    private fun cameraCallback(): ActivityResultCallback<ActivityResult> {
        return ActivityResultCallback {
            if (it.resultCode == Activity.RESULT_CANCELED) {
                callbackListener.onCancelled()
            } else if (it.resultCode == Activity.RESULT_OK) {

                val uri: Uri? =
                    ImageUtils.handleImageChooserData(it.data, imageCaptureFile, fragment.requireContext(), PhotoPickerBottomSheet.GALLERY_METHOD)

                if (uri == null) {
                    callbackListener.onImageNotFound()
                    return@ActivityResultCallback
                }
                // got image uri
                callbackListener.onSuccess(uri, ImageCaptureMethod.CAMERA)
            }
        }
    }

    private fun showImageFetchErrorToast() {
        Toast.makeText(
            fragment.requireContext().applicationContext,
            "Unable to fetch image. Try Again.",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        private const val TAG = "ImageCaptureManager"
    }
}

enum class ImageCaptureMethod {
    CAMERA, GALLERY, REMOVE
}