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
            //chiama la funziona al salvataggio del task
            scheduleNotification(name, date, time)

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
            checkTasksForToday() // <--- Controlla le scadenze appena caricato!
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
    // --- FUNZIONE PER CONTROLLARE SE C'È UN TASK OGGI ---
    private fun checkTasksForToday() {
        // 1. Otteniamo la data di oggi formattata come la salvi tu (dd/MM/yyyy)
        val calendar = Calendar.getInstance()
        val today = String.format("%02d/%02d/%04d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR))

        // 2. Scorriamo la lista dei task
        for (task in taskList) {
            // Il tuo formato è: "Nome, Ora, Data"
            // Dividiamo la stringa usando la virgola come separatore
            val parts = task.split(", ")

            if (parts.size >= 3) {
                val taskName = parts[0]
                val taskDate = parts[2] // La data è il terzo elemento

                // 3. Se la data del task è uguale a oggi... NOTIFICA!
                if (taskDate == today) {
                    sendTaskNotification(taskName)
                }
            }
        }
    }
    private fun sendTaskNotification(taskName: String) {
        val channelId = "todo_channel"
        val notifId = System.currentTimeMillis().toInt()

        // 1. Creiamo il canale (se non esiste)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId, "ToDo Scadenze", android.app.NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.createNotificationChannel(channel)
        }

        // 2. Prepariamo l'Intent per aprire TaskActionActivity
        val intent = android.content.Intent(this, TaskActionActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TASK_TITLE", taskName) // Passiamo il nome del compito
            putExtra("NOTIFICATION_ID", notifId)
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            this, notifId, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Costruiamo la notifica
        val builder = androidx.core.app.NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // O la tua R.drawable.ic_stat_name
            .setContentTitle("Scadenza Oggi!")
            .setContentText("Ricordati di: $taskName")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 4. Inviamo (con controllo permessi)
        with(androidx.core.app.NotificationManagerCompat.from(this)) {
            if (androidx.core.app.ActivityCompat.checkSelfPermission(
                    this@ToDoActivity,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notify(notifId, builder.build())
            }
        }
    }

    private fun scheduleNotification(taskName: String, dateStr: String, timeStr: String) {
        val calendar = Calendar.getInstance()
        // Parsiamo le stringhe (es. "30/10/2024" e "10:00")
        val dateParts = dateStr.split("/")
        val timeParts = timeStr.split(":")

        if (dateParts.size == 3 && timeParts.size == 2) {
            calendar.set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
            calendar.set(Calendar.MONTH, dateParts[1].toInt() - 1) // I mesi partono da 0
            calendar.set(Calendar.YEAR, dateParts[2].toInt())
            calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            calendar.set(Calendar.MINUTE, timeParts[1].toInt())
            calendar.set(Calendar.SECOND, 0)

            // Se l'orario è già passato, non schedulare
            if (calendar.timeInMillis <= System.currentTimeMillis()) return

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

            val intent = android.content.Intent(this, AlarmReceiver::class.java).apply {
                putExtra("TASK_TITLE", taskName)
            }

            // Usiamo un ID univoco basato sull'hash del nome task per non sovrapporli
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                this, taskName.hashCode(), intent, android.app.PendingIntent.FLAG_IMMUTABLE
            )

            // Impostiamo l'allarme esatto
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                    } else {
                        // Fallback o richiesta permessi (per ora usiamo setWindow che è meno restrittivo)
                        alarmManager.setWindow(android.app.AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 10000, pendingIntent)
                    }
                } else {
                    alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
                Toast.makeText(this, "Notifica programmata!", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                // Manca permesso SCHEDULE_EXACT_ALARM
            }
        }
    }



}
