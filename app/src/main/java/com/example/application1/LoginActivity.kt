package com.example.application1

import android.content.Context // Import necessario
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Setup UI
        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoToSignup = findViewById<Button>(R.id.btnGoToSignup)

        // --- SHARED PREFS: RECUPERO DATI ---
        // Apriamo il taccuino "LoginPrefs"
        val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        // Leggiamo i valori salvati (default stringa vuota)
        val savedEmail = sharedPref.getString("email", "")
        val savedPassword = sharedPref.getString("password", "")

        // Se c'è qualcosa, riempiamo i campi automaticamente
        etEmail.setText(savedEmail)
        etPassword.setText(savedPassword)
        // -----------------------------------

        if (auth.currentUser != null) {
            startActivity(Intent(this, LoggedActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            // --- SHARED PREFS: SALVATAGGIO DATI ---
                            // Login riuscito, salviamo le credenziali per la prossima volta
                            val editor = sharedPref.edit()
                            editor.putString("email", email)
                            editor.putString("password", password)
                            editor.apply() // Conferma salvataggio

                            Toast.makeText(this, "Credenziali salvate!", Toast.LENGTH_SHORT).show()
                            // -------------------------------------

                            startActivity(Intent(this, LoggedActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Login fallito: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        btnGoToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
