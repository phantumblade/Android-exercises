package com.example.application1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class Fragment3 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Carica il layout
        val view = inflater.inflate(R.layout.fragment_3, container, false)

        // 2. Trova il bottone
        val btnAction = view.findViewById<Button>(R.id.btnAction)

        // 3. Gestisci il click
        btnAction.setOnClickListener {
            Toast.makeText(context, "Funzionalità Modifica Profilo in arrivo!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
