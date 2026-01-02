package com.example.application1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestorePeopleActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var tvList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_people)

        val etName = findViewById<EditText>(R.id.etName)
        val etSurname = findViewById<EditText>(R.id.etSurname)
        val etAge = findViewById<EditText>(R.id.etAge)
        val btnAdd = findViewById<Button>(R.id.btnAddPerson)
        tvList = findViewById(R.id.tvPeopleList)

        // 1. ASCOLTO IN TEMPO REALE (Consumer)
        // Questa funzione parte subito e ogni volta che il database cambia
        db.collection("people")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    tvList.text = "Errore di ascolto: ${error.message}"
                    return@addSnapshotListener
                }

                // Costruiamo la stringa con i nuovi dati
                val sb = StringBuilder()
                if (value != null) {
                    for (document in value) {
                        val n = document.getString("name") ?: "-"
                        val s = document.getString("surname") ?: "-"
                        // L'età può essere salvata come stringa o numero, gestiamo entrambi
                        val a = document.get("age").toString()

                        sb.append("$n $s (Età: $a)\n\n")
                    }
                }
                tvList.text = sb.toString()
            }

        // 2. AGGIUNTA DATI (Producer)
        btnAdd.setOnClickListener {
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            val age = etAge.text.toString()

            if (name.isNotEmpty() && surname.isNotEmpty() && age.isNotEmpty()) {
                // Creiamo l'oggetto da inviare (Mappa chiave-valore)
                val personMap = hashMapOf(
                    "name" to name,
                    "surname" to surname,
                    "age" to age.toIntOrNull() // Proviamo a convertirlo in numero
                )

                db.collection("people")
                    .add(personMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Inviato!", Toast.LENGTH_SHORT).show()
                        // Puliamo i campi
                        etName.text.clear()
                        etSurname.text.clear()
                        etAge.text.clear()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Errore invio", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Compila tutto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
