package com.nick.imagepickerandroid

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.nick.imagepickerandroid.adapters.ListImagesAdapter
import com.nick.imagepickerandroid.databinding.ActivityMainBinding
import com.nick.imagepickerandroid.image_picker.ImagePicker.initPickAnImageFromGalleryResultLauncher
import com.nick.imagepickerandroid.image_picker.ImagePicker.initPickMultipleImagesFromGalleryResultLauncher
import com.nick.imagepickerandroid.image_picker.ImagePicker.initTakePhotoWithCameraResultLauncher
import com.nick.imagepickerandroid.image_picker.ImagePicker.pickAnImageFromGallery
import com.nick.imagepickerandroid.image_picker.ImagePicker.pickMultipleImagesFromGallery
import com.nick.imagepickerandroid.image_picker.ImagePicker.takeAPhotoWithCamera
import com.nick.imagepickerandroid.image_picker.ImagePickerInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ImagePickerInterface {

    private lateinit var binding: ActivityMainBinding
    private var listImageAdapter: ListImagesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initImagePicker()
        initAdapter()
        initListeners()
    }

    private fun initAdapter() {
        listImageAdapter = ListImagesAdapter(mutableListOf())
        binding.recyclerView.adapter = listImageAdapter
    }

    private fun initImagePicker() {
        initPickAnImageFromGalleryResultLauncher(
            fragmentActivity = this,
            imagePickerInterface = this
        )
        initPickMultipleImagesFromGalleryResultLauncher(
            fragmentActivity = this,
            coroutineScope = lifecycleScope,
            imagePickerInterface = this
        )
        initTakePhotoWithCameraResultLauncher(
            fragmentActivity = this,
            imagePickerInterface = this
        )
    }

    private fun initListeners() {
        binding.pickImage.setOnClickListener {
            pickAnImageFromGallery(fragmentActivity = this)
        }
        binding.pickImages.setOnClickListener {
            pickMultipleImagesFromGallery(fragmentActivity = this)
        }
        binding.camera.setOnClickListener {
            takeAPhotoWithCamera(fragmentActivity = this)
        }
    }

    /**
     * Call Back - Get Bitmap and Uri
     * */
    override fun onGalleryImage(bitmap: Bitmap?, uri: Uri?) {
        if (bitmap != null) binding.image.setImageBitmap(bitmap)
        super.onGalleryImage(bitmap, uri)
    }

    override fun onCameraImage(bitmap: Bitmap?, uri: Uri?) {
        if (bitmap != null) binding.image.setImageBitmap(bitmap)
        super.onCameraImage(bitmap, uri)
    }

    override fun onMultipleGalleryImages(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (bitmapList != null) listImageAdapter?.loadData(bitmapList)
        }
        super.onMultipleGalleryImages(bitmapList, uriList)
    }
}