package com.example.application1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.application1.databinding.ActivityLoggedBinding
import com.google.firebase.auth.FirebaseAuth

class LoggedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoggedBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Mostriamo l'email dell'utente
        val user = auth.currentUser
        if (user != null) {
            binding.tvWelcome.text = "Ciao,\n${user.email}"
        }

        // Tasto Logout
        binding.btnLogout.setOnClickListener {
            auth.signOut() // Disconnette da Firebase

            // Torna alla schermata di Login
            val intent = Intent(this, LoginActivity::class.java)
            // Pulisce la storia (l'utente non può tornare qui premendo "Indietro")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
    