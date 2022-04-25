package com.pucosa.pucopointManager.ui.photoPickerBottomSheet

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pucosa.pucopointManager.databinding.FragmentPhotoPickerBottomSheetBinding

class PhotoPickerBottomSheet(private val listeners: PhotoOptionClickListeners, private val removeImage: Boolean = false): BottomSheetDialogFragment() {

    private var _binding: FragmentPhotoPickerBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val gradientColors = arrayOf(
        mapOf("start" to Color.parseColor("#ff9a9e"), "end" to Color.parseColor("#fad0c4")),
        mapOf("start" to Color.parseColor("#c1dfc4"), "end" to Color.parseColor("#deecdd")),
        mapOf("start" to Color.parseColor("#accbee"), "end" to Color.parseColor("#e7f0fd"))
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoPickerBottomSheetBinding.inflate(inflater, container, false)

        //set container background
        val parentBackground = GradientDrawable()
        parentBackground.shape = GradientDrawable.RECTANGLE
        parentBackground.cornerRadii = floatArrayOf(5f, 5f, 5f, 5f, 0f, 0f, 0f, 0f)
        binding.root.background = parentBackground

        setBackgrounds()

        if(!removeImage) {
            binding.deleteParent.visibility = View.GONE
        }
        else {
            binding.deleteParent.setOnClickListener {
                listeners.onRemoveSelected(this)
            }
        }

        binding.galleryParent.setOnClickListener {
            listeners.onGallerySelected(this)
        }

        binding.cameraParent.setOnClickListener {
            listeners.onCameraSelected(this)
        }

        return binding.root
    }

    private fun setBackgrounds() {
        val imgViews = arrayListOf(binding.deleteImageView, binding.galleryImageView, binding.cameraImageView)
        imgViews.forEachIndexed { i, imageView ->
            val gD = GradientDrawable()
            gD.colors = intArrayOf(gradientColors[i]["start"]!!, gradientColors[i]["end"]!!)
            gD.orientation = GradientDrawable.Orientation.LEFT_RIGHT
            gD.shape = GradientDrawable.OVAL
            gD.gradientType = GradientDrawable.LINEAR_GRADIENT
            imageView.background = gD
        }
    }


    companion object {
        private const val TAG = "PhotoPickerBottomSheet"

        const val CAMERA_METHOD = "camera"
        const val GALLERY_METHOD = "gallery"
        const val REMOVE_METHOD = "remove"
    }
}