package com.example.application1 // ASSICURATI CHE QUESTO PACKAGE SIA IL TUO!

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PositiveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Collega questa classe al layout che abbiamo appena creato
        setContentView(R.layout.activity_positive)
        // 1. Cerco la TextView dove voglio scrivere il messaggio
        val textView: TextView = findViewById(R.id.welcomeMsg)

        //caso 1: arrio dal bottone "Nuova pagina (intent esplicito)"
        if(intent.hasExtra("CHIAVE_EMAIL")) {
            val email = intent.getStringExtra("CHIAVE_EMAIL")
            textView.text = "Ciao $email, sei arrivato dal bottone!"
        }
        //Caso 2: arrivo da un link web (intent implicito -> il filtro)
        // 'intent.data' contiene l'URL che ha fatto scattare il fitro
        else if(intent.data != null){
            val urlCliccato = intent.data.toString()
            textView.text = "Wow! Hai chiesto di aprire questo sito:\n$urlCliccato\n\n(Ma io non sono un vero browser, ahah!)"
        }
        else{
            textView.text = "Benvenuto!"
        }
    }
}
