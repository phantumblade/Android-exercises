package com.example.application1

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.application1.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 1. Tasto ACCEDI
        binding.btnLogin.setOnClickListener {
            if (isInputValid()) {
                performLogin()
            }
        }

        // 2. Tasto REGISTRATI
        binding.btnRegister.setOnClickListener {
            if (isInputValid()) {
                performRegistration() // Ora chiama la funzione corretta
            }
        }
    }

    // --- Controlli all'avvio ---
    override fun onStart() {
        super.onStart()
        // Se l'utente è già loggato, vai diretto alla schermata di Logout/Benvenuto
        if (auth.currentUser != null) {
            goToLoggedScreen()
        }
    }

    // --- Validazione ---
    private fun isInputValid(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        var isValid = true

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email non valida"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.length < 6) {
            binding.tilPassword.error = "Minimo 6 caratteri"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }
        return isValid
    }

    // --- Login ---
    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Login Riuscito!", Toast.LENGTH_SHORT).show()
                    goToLoggedScreen()
                } else {
                    Toast.makeText(baseContext, "Errore Login: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // --- Registrazione ---
    private fun performRegistration() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Registrato con successo!", Toast.LENGTH_SHORT).show()
                    goToLoggedScreen()
                } else {
                    Toast.makeText(baseContext, "Errore Registrazione: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // --- Navigazione ---
    private fun goToLoggedScreen() {
        // Invece di MainActivity, andiamo alla nuova LoggedActivity
        val intent = Intent(this, LoggedActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
