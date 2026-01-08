package com.example.application1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class CityDetailFragment : Fragment() {

    private lateinit var cityImage: ImageView
    private lateinit var cityDescription: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Colleghiamo il layout XML che abbiamo creato prima
        val view = inflater.inflate(R.layout.fragment_city_detail, container, false)

        // Troviamo le view grafiche
        cityImage = view.findViewById(R.id.city_image)
        cityDescription = view.findViewById(R.id.city_description)

        return view
    }

    // --- METODO PUBBLICO CHIAMATO DALL'ACTIVITY ---
    // Questo è il punto chiave: l'Activity chiama questa funzione quando
    // viene cliccata una città nella lista.
    fun updateContent(cityName: String) {

        // Aggiorna il testo
        cityDescription.text = "Ecco una bella vista di $cityName"

        // Aggiorna l'immagine in base al nome (simuliamo con icone di sistema o risorse)
        // Se avessi foto reali in res/drawable potresti usare R.drawable.roma, etc.
        when (cityName) {
            "Milano" -> cityImage.setImageResource(R.drawable.milano) // Cerca milano.jpg in drawable
            "Roma" -> cityImage.setImageResource(R.drawable.roma)     // Cerca roma.jpg
            "Napoli" -> cityImage.setImageResource(R.drawable.napoli) // Cerca napoli.jpg
            "Firenze" -> cityImage.setImageResource(R.drawable.firenze)
            "Torino" -> cityImage.setImageResource(R.drawable.torino)
            else -> cityImage.setImageResource(android.R.drawable.ic_menu_help)
        }
    }
}
