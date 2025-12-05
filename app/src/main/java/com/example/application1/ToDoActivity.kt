package com.example.application1

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

class ToDoActivity : AppCompatActivity() {

    private val FILE_NAME = "tasks.txt"
    private lateinit var listView: ListView
    private lateinit var adapter: TaskAdapter
    private val taskList = ArrayList<String>() // Lista che tiene i dati in memoria

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        // 1. Trova i componenti
        val etName = findViewById<EditText>(R.id.etTaskName)
        val etDate = findViewById<EditText>(R.id.etTaskDate)
        val etTime = findViewById<EditText>(R.id.etTaskTime)
        val btnSave = findViewById<Button>(R.id.btnSaveTask)
        listView = findViewById(R.id.listViewTasks)

        // --- NOVITÀ: Configurazione DatePicker ---
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Formattiamo la data (es. 30/10/2023)
                    val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                    etDate.setText(formattedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        // --- NOVITÀ: Configurazione TimePicker ---
        etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    // Formattiamo l'orario (es. 14:05)
                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    etTime.setText(formattedTime)
                },
                hour, minute, true // true = usa formato 24 ore
            )
            timePickerDialog.show()
        }

        // 2. Configura la lista
        adapter = TaskAdapter(this, taskList)
        listView.adapter = adapter

        // 3. Carica i dati salvati all'avvio
        loadTasksFromFile()

        // 4. Gestisci il salvataggio
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val date = etDate.text.toString()
            val time = etTime.text.toString()

            if (name.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Formatta la stringa come richiesto: "Nome, Ora, Data"
            val taskLine = "$name, $time, $date"

            saveTaskToFile(taskLine)

            // Aggiungi alla lista visiva e pulisci i campi
            taskList.add(taskLine)
            adapter.notifyDataSetChanged()

            etName.text.clear()
            etDate.text.clear()
            etTime.text.clear()
        }
    }

    // --- FUNZIONE PER SALVARE (APPEND) ---
    private fun saveTaskToFile(data: String) {
        try {
            openFileOutput(FILE_NAME, Context.MODE_APPEND).use {
                it.write((data + "\n").toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- FUNZIONE PER LEGGERE TUTTO ---
    private fun loadTasksFromFile() {
        taskList.clear()
        try {
            val fileInputStream = openFileInput(FILE_NAME)
            val reader = BufferedReader(InputStreamReader(fileInputStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (!line.isNullOrEmpty()) {
                    taskList.add(line!!)
                }
            }
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            // File non esistente, ignora
        }
    }

    // --- FUNZIONE PER CANCELLARE ---
    fun deleteTask(position: Int) {
        taskList.removeAt(position)
        adapter.notifyDataSetChanged()

        try {
            openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { stream ->
                for (task in taskList) {
                    stream.write((task + "\n").toByteArray())
                }
            }
            Toast.makeText(this, "Compito eliminato", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- ADAPTER PERSONALIZZATO ---
    inner class TaskAdapter(context: Context, private val data: ArrayList<String>) :
        ArrayAdapter<String>(context, R.layout.todo_item, data) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false)

            val tvDetails = view.findViewById<TextView>(R.id.tvTaskDetails)
            val btnDelete = view.findViewById<Button>(R.id.btnDeleteTask)

            val itemText = data[position]
            tvDetails.text = itemText

            btnDelete.setOnClickListener {
                deleteTask(position)
            }

            return view
        }
    }
}
