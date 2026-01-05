package com.example.application1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class FirestorePeopleActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var adapter: PeopleAdapter
    private val peopleList = ArrayList<PersonModel>()

    // Per le notifiche
    private val CHANNEL_ID = "firestore_channel"
    private var isFirstLoad = true
    private lateinit var myDeviceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_people)

        // 1. Setup ID e Notifiche
        myDeviceId = getAppUniqueId()
        createNotificationChannel()

        val etName = findViewById<EditText>(R.id.etName)
        val etSurname = findViewById<EditText>(R.id.etSurname)
        val etAge = findViewById<EditText>(R.id.etAge)
        val btnAdd = findViewById<Button>(R.id.btnAddPerson)
        val btnDeleteAll = findViewById<ImageButton>(R.id.btnDeleteAll)
        val recyclerView = findViewById<RecyclerView>(R.id.rvPeople)

        // 2. Configura RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PeopleAdapter(peopleList) { idDaCancellare ->
            deleteSinglePerson(idDaCancellare)
        }
        recyclerView.adapter = adapter

        // 3. ASCOLTO REALTIME (Consumer + Notifiche)
        db.collection("people")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Errore sync: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (value != null) {
                    val tempPeople = ArrayList<PersonModel>()

                    for (document in value) {
                        val person = PersonModel(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            surname = document.getString("surname") ?: "",
                            age = document.getLong("age")?.toInt() ?: 0
                        )
                        tempPeople.add(person)
                    }

                    // Aggiorna la lista grafica
                    adapter.updateData(tempPeople)

                    // LOGICA NOTIFICHE
                    if (!isFirstLoad) {
                        for (dc in value.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                val senderId = dc.document.getString("senderId")
                                // Se l'ID è diverso dal mio, notifica!
                                if (senderId != myDeviceId) {
                                    val newName = dc.document.getString("name") ?: "Qualcuno"
                                    val newSurname = dc.document.getString("surname") ?: ""
                                    sendNotification(newName, newSurname)
                                }
                            }
                        }
                    }
                    isFirstLoad = false
                }
            }

        // 4. AGGIUNGI (Producer)
        btnAdd.setOnClickListener {
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            val ageStr = etAge.text.toString()

            if (name.isNotEmpty() && surname.isNotEmpty() && ageStr.isNotEmpty()) {
                val personMap = hashMapOf(
                    "name" to name,
                    "surname" to surname,
                    "age" to ageStr.toInt(),
                    "senderId" to myDeviceId // Firma il messaggio
                )

                db.collection("people").add(personMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Persona aggiunta con successo!", Toast.LENGTH_SHORT).show()
                        etName.text.clear()
                        etSurname.text.clear()
                        etAge.text.clear()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Errore inserimento", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. ELIMINA TUTTO
        btnDeleteAll.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Elimina Tutto")
                .setMessage("Vuoi cancellare l'intero database?")
                .setPositiveButton("Sì") { _, _ -> deleteAllPeople() }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun deleteSinglePerson(docId: String) {
        db.collection("people").document(docId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Elemento eliminato", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteAllPeople() {
        db.collection("people").get().addOnSuccessListener { result ->
            for (document in result) {
                document.reference.delete()
            }
            Toast.makeText(this, "Database pulito!", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Gestione ID e Notifiche ---

    private fun getAppUniqueId(): String {
        val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        var id = sharedPrefs.getString("device_uuid", null)
        if (id == null) {
            id = UUID.randomUUID().toString()
            sharedPrefs.edit().putString("device_uuid", id).apply()
        }
        return id!!
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Nuove Persone"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(name: String, surname: String) {
        val intent = Intent(this, FirestorePeopleActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_add)
            .setContentTitle("Nuova voce nel DB!")
            .setContentText("$name $surname è stato aggiunto.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
