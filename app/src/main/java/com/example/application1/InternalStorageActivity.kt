package com.example.application1

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class InternalStorageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internal_storage)
        title = "Esercizio Internal Storage"

        //1 - collegamento delle variabili alla grafica xml
        val etFileName = findViewById<EditText>(R.id.etFileName)
        val etFileContent = findViewById<EditText>(R.id.etFileContent)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnRead = findViewById<Button>(R.id.btnRead)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        //Salvataggio (WRITE) parte 1

        //Quando clicchi SALVA prendiamo il testo e lo scriviamo sul file
        btnSave.setOnClickListener {
            val fileName = etFileName.text.toString()
            val content = etFileContent.text.toString()
            //sicurezza: i campi sono vuoti?
            if (fileName.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                // 'openFileOutput': Crea un flusso verso un file privato
                // MODE_PRIVATE: Se esiste sovrascrivi, altrimenti crea. Solo la nostra app può vederlo
                val fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
                //Scrivimo i dati convertiti in byte
                fileOutputStream.write(content.toByteArray())
                //Chiusura file per salvare le modifiche
                fileOutputStream.close()
                Toast.makeText(this, "Salvataggio riuscito", Toast.LENGTH_SHORT).show()
                etFileContent.text.clear() // pulizia del campo per vedere il il salvataggio riuscito
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
            }
        }

        //Lettura (READ) parte 2

        // al click di LEGGI cerca il file con il nome corretto
        btnRead.setOnClickListener {
            val fileName = etFileName.text.toString()
            if (fileName.isEmpty()){
                Toast.makeText(this, "Compila il campo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                // Apre il file in lettura
                val fileInputStream = openFileInput(fileName)
                val inputStreamReader = InputStreamReader(fileInputStream)

                // BufferedReader è efficiente per leggere righe di testo
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()
                var text: String? = null
                //lettura fino a fine file
                while ({text = bufferedReader.readLine(); text}()!=null){
                    stringBuilder.append(text).append("\n")
                }
                fileInputStream.close()
                // Mostriamo il risultato a schermo
                tvResult.text = "CONTENUTO DEL FILE:\n$stringBuilder"
            } catch (e: Exception){
                tvResult.text = "Errore: File non trovato!"
                Toast.makeText(this, "Il file non esiste", Toast.LENGTH_SHORT).show()
            }
        }

        //Cancellazione (DELETE) del file, parte 3
        btnDelete.setOnClickListener {
            val fileName = etFileName.text.toString()

            // deleteFile restituisce TRUE se ha funzionato
            if (deleteFile(fileName)) {
                Toast.makeText(this, "File eliminato per sempre", Toast.LENGTH_SHORT).show()
                tvResult.text = "File cancellato."
                etFileContent.text.clear()
            } else {
                Toast.makeText(this, "File non trovato, impossibile cancellare", Toast.LENGTH_SHORT).show()
            }
        }
    }
}