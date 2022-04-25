package com.pucosa.pucopointManager.ui.photoPickerBottomSheet

import android.util.Log

interface PhotoOptionClickListeners {
    fun onRemoveSelected(photoPickerBottomSheet: PhotoPickerBottomSheet) {
        Log.i(TAG, "onDeleteClicked: delete clicked")
    }

    fun onGallerySelected(photoPickerBottomSheet: PhotoPickerBottomSheet)
    fun onCameraSelected(photoPickerBottomSheet: PhotoPickerBottomSheet)

    companion object {
        private const val TAG = "PhotoOptionClickListene"
    }
}