package com.example.application1

import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

class ExternalStorageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_external_storage)
        title = "External Storage Exercise"

        val etFileName = findViewById<EditText>(R.id.etExtFileName)
        val etFileContent = findViewById<EditText>(R.id.etExtFileContent)
        val tvResult = findViewById<TextView>(R.id.tvExtResult)
        val btnSave = findViewById<Button>(R.id.btnExtSave)
        val btnRead = findViewById<Button>(R.id.btnExtRead)

        // --- SALVATAGGIO SU EXTERNAL STORAGE ---
        btnSave.setOnClickListener {
            val fileName = etFileName.text.toString()
            val content = etFileContent.text.toString()

            if (fileName.isEmpty() || content.isEmpty()) return@setOnClickListener

            // 1. Verifica se la memoria esterna è disponibile (montata)
            if (isExternalStorageWritable()) {
                // 2. Ottieni il percorso della cartella esterna specifica dell'app
                // Percorso tipico: /storage/emulated/0/Android/data/com.example.application1/files/
                val externalDir = getExternalFilesDir(null)

                // 3. Crea l'oggetto File puntando a quella cartella
                val myFile = File(externalDir, fileName)

                try {
                    // 4. Usa FileOutputStream classico di Java
                    val fos = FileOutputStream(myFile)
                    fos.write(content.toByteArray())
                    fos.close()

                    Toast.makeText(this, "Salvato in External Storage!", Toast.LENGTH_LONG).show()
                    tvResult.text = "File salvato in:\n${myFile.absolutePath}"
                    etFileContent.text.clear()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Errore salvataggio: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Errore: Memoria esterna non disponibile", Toast.LENGTH_LONG).show()
            }
        }

        // --- LETTURA DA EXTERNAL STORAGE ---
        btnRead.setOnClickListener {
            val fileName = etFileName.text.toString()
            if (fileName.isEmpty()) return@setOnClickListener

            if (isExternalStorageReadable()) {
                val externalDir = getExternalFilesDir(null)
                val myFile = File(externalDir, fileName)

                try {
                    // Usa FileInputStream classico di Java
                    val fis = FileInputStream(myFile)
                    val inputStreamReader = InputStreamReader(fis)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    val stringBuilder = StringBuilder()
                    var text: String? = null

                    while ({ text = bufferedReader.readLine(); text }() != null) {
                        stringBuilder.append(text).append("\n")
                    }
                    fis.close()

                    tvResult.text = "CONTENUTO FILE ESTERNO:\n$stringBuilder"
                } catch (e: Exception) {
                    tvResult.text = "Errore: File non trovato o illeggibile."
                }
            }
        }
    }

    // Funzione ausiliaria per controllare se possiamo SCRIVERE
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // Funzione ausiliaria per controllare se possiamo LEGGERE
    private fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }
}
