package com.example.application1

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore // <--- IMPORTANTE
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class RegistrationProActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    // Variabile per capire se l'utente ha scelto una foto
    private var isPhotoSelected = false

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    // 1. Launcher per SCATTARE FOTO
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            imgProfile.setImageBitmap(bitmap)
            imgProfile.setPadding(0, 0, 0, 0)
            isPhotoSelected = true
        }
    }

    // 2. Launcher per CERCARE IN GALLERIA
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imgProfile.setImageURI(uri)
            imgProfile.setPadding(0, 0, 0, 0)
            isPhotoSelected = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_pro)

        // Inizializza Firebase
        auth = FirebaseAuth.getInstance()

        // Se hai creato un bucket manuale, usa il suo indirizzo qui, altrimenti usa .getInstance() vuoto
        // Se ti dava problemi prima, lascia pure l'indirizzo specifico che avevi
        storage = FirebaseStorage.getInstance("gs://firstapplication-6f424.firebasestorage.app")

        // Binding
        imgProfile = findViewById(R.id.imgProfile)
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnGallery = findViewById<Button>(R.id.btnGallery)
        etName = findViewById(R.id.etName)
        etSurname = findViewById(R.id.etSurname)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegisterUser)

        btnCamera.setOnClickListener { takePictureLauncher.launch(null) }
        btnGallery.setOnClickListener { pickImageLauncher.launch("image/*") }

        btnRegister.setOnClickListener {
            if (validateForm()) {
                performRegistration()
            }
        }
    }

    private fun validateForm(): Boolean {
        if (etName.text.isEmpty() || etSurname.text.isEmpty() ||
            etEmail.text.isEmpty() || etPassword.text.isEmpty()) {
            Toast.makeText(this, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun performRegistration() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        // 1. Crea l'utente su Firebase Auth (Login)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                Toast.makeText(this, "Account creato! Salvataggio dati...", Toast.LENGTH_SHORT).show()

                if (userId != null) {
                    // 2. Se c'è la foto, caricala su Storage -> Poi salva su Firestore
                    if (isPhotoSelected) {
                        uploadImageToStorage(userId)
                    } else {
                        // 3. Se NON c'è la foto, salva SUBITO su Firestore (senza url)
                        saveUserToFirestore(userId, "")
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errore registrazione: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImageToStorage(userId: String) {
        val storageRef = storage.reference.child("profile_images/${userId}.jpg")

        imgProfile.isDrawingCacheEnabled = true
        imgProfile.buildDrawingCache()
        val bitmap = (imgProfile.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)

        uploadTask.addOnSuccessListener {
            // FOTO CARICATA -> Ora prendiamo l'URL pubblico per salvarlo nel DB
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                // Abbiamo l'URL: Ora salviamo l'utente nel Database
                saveUserToFirestore(userId, uri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Errore foto, salvo senza immagine", Toast.LENGTH_SHORT).show()
            // Se fallisce la foto, salviamo comunque l'utente nel DB
            saveUserToFirestore(userId, "")
        }
    }

    // --- NUOVA FUNZIONE FONDAMENTALE ---
    // Questa funzione scrive i dati dentro la collezione "users" di Firestore
    private fun saveUserToFirestore(userId: String, profileImageUrl: String) {
        val db = FirebaseFirestore.getInstance()

        // Creiamo la mappa dei dati da salvare
        val userMap = hashMapOf(
            "uid" to userId,
            "name" to etName.text.toString(),
            "surname" to etSurname.text.toString(),
            "email" to etEmail.text.toString(),
            "profileImage" to profileImageUrl
        )

        // Scrittura nel database
        db.collection("users").document(userId).set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Registrazione completata e salvata!", Toast.LENGTH_LONG).show()
                finish() // Chiude la pagina e torna indietro
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errore salvataggio DB: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun finishRegistration() {
        // Questa funzione ora è sostituita da saveUserToFirestore che chiama finish() alla fine
        finish()
    }
}
