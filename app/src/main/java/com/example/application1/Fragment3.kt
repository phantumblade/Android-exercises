package com.example.application1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.InputStreamReader

class Fragment3 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Carica il layout
        val view = inflater.inflate(R.layout.fragment_3, container, false)

        // Trova i componenti
        val etFileName = view.findViewById<EditText>(R.id.etFileName)
        val etFileData = view.findViewById<EditText>(R.id.etFileData)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnView = view.findViewById<Button>(R.id.btnView)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnClear = view.findViewById<Button>(R.id.btnClear)



        // --- LOGICA BOTTONE SAVE ---
        btnSave.setOnClickListener {
            val filename = etFileName.text.toString()
            val fileContents = etFileData.text.toString()

            if (filename.isEmpty()) {
                etFileName.error = "Inserisci un nome file!"
                return@setOnClickListener
            }

            try {
                // Apre (o crea) il file in modalità PRIVATA (solo questa app può leggerlo)
                requireContext().openFileOutput(filename, Context.MODE_PRIVATE).use { stream ->
                    stream.write(fileContents.toByteArray())
                }

                Toast.makeText(context, "Dati salvati in $filename", Toast.LENGTH_SHORT).show()

                // Pulisce i campi dopo il salvataggio
                etFileData.text.clear()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
            }
        }

        // --- LOGICA BOTTONE VIEW ---
        btnView.setOnClickListener {
            val filename = etFileName.text.toString()

            if (filename.isEmpty()) {
                etFileName.error = "Inserisci il nome file da leggere!"
                return@setOnClickListener
            }

            try {
                // Apre il file in lettura
                val fileInputStream = requireContext().openFileInput(filename)
                val inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)

                val stringBuilder = StringBuilder()
                var text: String? = null

                // Legge riga per riga
                while ({ text = bufferedReader.readLine(); text }() != null) {
                    stringBuilder.append(text)
                }

                // Mostra il risultato nella casella di testo
                etFileData.setText(stringBuilder.toString())

                Toast.makeText(context, "File letto con successo!", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "File non trovato o errore di lettura", Toast.LENGTH_SHORT).show()
            }
        }

        //Logica del button DELETE
        btnDelete.setOnClickListener {
            val filename = etFileName.text.toString()
            if(filename.isEmpty()){
                etFileName.error = "Nome file mancante!"
                return@setOnClickListener
            }
            //rutornerà TRUE se ha cancellato correttamente, FALSE altrimenti
            if(requireContext().deleteFile(filename)){
                Toast.makeText(context, "File cancellato con successo!", Toast.LENGTH_SHORT).show()
                //pulizia dei campi
                etFileName.text.clear()
                etFileData.text.clear()
            }else{
                Toast.makeText(context, "Errore nella cancellazione del file!", Toast.LENGTH_SHORT).show()
            }

        }

        //logica del botttone CLEAR
        btnClear.setOnClickListener {
            etFileName.text.clear()
            etFileData.text.clear()
            Toast.makeText(context, "Campi svuotati puliti!", Toast.LENGTH_SHORT).show()
        }

        return view


    }

}
