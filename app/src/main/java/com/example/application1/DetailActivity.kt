package com.example.application1
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Riceviamo i dati
        val nome = intent.getStringExtra("NOME")
        val cognome = intent.getStringExtra("COGNOME")
        val eta = intent.getIntExtra("ETA", 0)

        // Visualizziamo
        findViewById<TextView>(R.id.det_nome_page).text = nome
        findViewById<TextView>(R.id.det_cognome_page).text = cognome
        findViewById<TextView>(R.id.det_eta_page).text = getString(R.string.age_format, eta)
    }
}
