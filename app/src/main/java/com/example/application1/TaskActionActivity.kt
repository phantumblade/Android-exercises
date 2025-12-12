package com.example.application1

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar
import java.util.ArrayList

class TaskActionActivity : AppCompatActivity() {

    private val FILE_NAME = "tasks.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_action)

        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: return
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)

        findViewById<TextView>(R.id.tvTaskTitle).text = "Scadenza: $taskTitle"

        // Rimuovi notifica dalla barra
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(notificationId)

        // --- BOTTONE CANCELLA ---
        findViewById<Button>(R.id.btnDeleteTask).setOnClickListener {
            modifyTaskInFile(taskTitle, delete = true)
            Toast.makeText(this, "Task Cancellato!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // --- BOTTONE POSTICIPA (Ora funziona!) ---
        findViewById<Button>(R.id.btnPostponeTask).setOnClickListener {
            modifyTaskInFile(taskTitle, delete = false) // false = posticipa
            Toast.makeText(this, "Task rimandato a domani!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Funzione UNICA che gestisce sia cancellazione che posticipo
    private fun modifyTaskInFile(taskNameTarget: String, delete: Boolean) {
        try {
            val tempTaskList = ArrayList<String>()
            val fileInputStream = openFileInput(FILE_NAME)
            val reader = BufferedReader(InputStreamReader(fileInputStream))
            var line: String?

            // Calcoliamo la data di domani per il posticipo
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrow = String.format("%02d/%02d/%04d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR))

            while (reader.readLine().also { line = it } != null) {
                if (!line.isNullOrEmpty()) {
                    // Formato riga: "Nome, Ora, Data"
                    val parts = line!!.split(", ")

                    if (parts.isNotEmpty() && parts[0] == taskNameTarget) {
                        if (delete) {
                            // Se dobbiamo cancellare, NON aggiungiamo la riga alla lista temporanea
                            continue
                        } else {
                            // Se dobbiamo posticipare, modifichiamo la data (parte 2)
                            // Ricostruiamo la stringa con la nuova data
                            if (parts.size >= 3) {
                                val newTask = "${parts[0]}, ${parts[1]}, $tomorrow"
                                tempTaskList.add(newTask)
                            }
                        }
                    } else {
                        // Se non è il task che cerchiamo, lo copiamo uguale
                        tempTaskList.add(line!!)
                    }
                }
            }
            fileInputStream.close()

            // Riscriviamo il file
            openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { stream ->
                for (task in tempTaskList) {
                    stream.write((task + "\n").toByteArray())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
