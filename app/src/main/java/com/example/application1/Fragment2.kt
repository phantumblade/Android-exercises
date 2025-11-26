package com.example.application1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Fragment2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Carica il layout XML
        val view = inflater.inflate(R.layout.fragment_2, container, false)

        // 2. Trova la RecyclerView dentro la vista appena caricata
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFragment2)

        // 3. Imposta il LayoutManager (IMPORTANTE)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 4. Prepara i dati (Puoi crearne di nuovi o copiare quelli della MainActivity)
        val listaDati = ArrayList<ItemsViewModel>()
        for (i in 1..15) {
            // Nota: Assicurati di avere l'immagine 'android_button_3d' o usa 'ic_launcher_foreground'
            listaDati.add(ItemsViewModel(R.drawable.ic_launcher_foreground, "Elemento Fragment $i"))
        }

        // 5. Collega l'Adapter (Riutilizziamo CustomAdapter che hai già)
        val adapter = CustomAdapter(listaDati)
        recyclerView.adapter = adapter

        return view
    }
}
