package com.example.application1

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class CameraActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private var imageUri: Uri? = null

    // 1. Launcher per i Permessi
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Permesso Camera negato", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Launcher per il risultato della Fotocamera
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // La foto è stata scattata e salvata nell'URI che abbiamo creato prima
            imgProfile.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        imgProfile = findViewById(R.id.imgProfile)
        val btnTakePhoto = findViewById<Button>(R.id.btnTakePhoto)

        btnTakePhoto.setOnClickListener {
            checkPermissionsAndOpenCamera()
        }
    }

    private fun checkPermissionsAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Abbiamo già il permesso
            openCamera()
        } else {
            // Chiediamo il permesso
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Nuova Foto Profilo")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Dalla Camera App")

        // Creiamo un placeholder vuoto nella galleria dove salvare la foto
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        takePictureLauncher.launch(cameraIntent)
    }
}
