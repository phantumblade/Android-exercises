package com.example.application1 // Assicurati che questo sia il nome del tuo package

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreQuotesActivity : AppCompatActivity() {

    // Componenti UI
    private lateinit var etAuthor: EditText
    private lateinit var etQuote: EditText
    private lateinit var tvResult: TextView
    private lateinit var btnAdd: Button
    private lateinit var btnGet: Button

    // Istanza di Firestore
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_quotes)

        // Collega le variabili ai componenti nel layout XML
        etAuthor = findViewById(R.id.etAuthor)
        etQuote = findViewById(R.id.etQuote)
        tvResult = findViewById(R.id.tvResult)
        btnAdd = findViewById(R.id.btnAdd)
        btnGet = findViewById(R.id.btnGet)

        // Imposta l'azione per il bottone "ADD"
        btnAdd.setOnClickListener {
            addQuoteToFirestore()
        }

        // Imposta l'azione per il bottone "GET"
        btnGet.setOnClickListener {
            getQuotesFromFirestore()
        }
    }

    private fun addQuoteToFirestore() {
        val authorText = etAuthor.text.toString()
        val quoteText = etQuote.text.toString()

        if (authorText.isNotEmpty() && quoteText.isNotEmpty()) {
            val quoteData = hashMapOf(
                "author" to authorText,
                "quote" to quoteText
            )

            db.collection("quotes")
                .add(quoteData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Salvato con successo!", Toast.LENGTH_SHORT).show()
                    etAuthor.text.clear()
                    etQuote.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Errore nel salvataggio: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Riempi entrambi i campi!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getQuotesFromFirestore() {
        tvResult.text = "Caricamento in corso..." // Feedback per l'utente
        db.collection("quotes")
            .get()
            .addOnSuccessListener { result ->
                val stringBuilder = StringBuilder()
                if (result.isEmpty) {
                    stringBuilder.append("Nessuna citazione trovata.")
                } else {
                    for (document in result) {
                        val autore = document.getString("author") ?: "N/D"
                        val citazione = document.getString("quote") ?: "N/D"
                        stringBuilder.append("Autore: $autore\nCitazione: \"$citazione\"\n\n")
                    }
                }
                tvResult.text = stringBuilder.toString()
            }
            .addOnFailureListener { exception ->
                tvResult.text = "Errore nel caricamento: ${exception.message}"
            }
    }
}
