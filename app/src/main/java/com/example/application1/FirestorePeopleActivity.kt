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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class FirestorePeopleActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var tvList: TextView
    private val CHANNEL_ID = "firestore_channel"
    private var isFirstLoad = true

    // ID Univoco di questo telefono
    private lateinit var myDeviceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_people)

        // 1. Recuperiamo o creiamo l'ID di questo telefono
        myDeviceId = getAppUniqueId()

        createNotificationChannel()

        val etName = findViewById<EditText>(R.id.etName)
        val etSurname = findViewById<EditText>(R.id.etSurname)
        val etAge = findViewById<EditText>(R.id.etAge)
        val btnAdd = findViewById<Button>(R.id.btnAddPerson)
        tvList = findViewById(R.id.tvPeopleList)

        // 2. ASCOLTO (Consumer)
        db.collection("people")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    tvList.text = "Errore: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val sb = StringBuilder()
                    for (document in snapshots) {
                        val n = document.getString("name") ?: "-"
                        val s = document.getString("surname") ?: "-"
                        val a = document.get("age").toString()
                        sb.append("$n $s (Età: $a)\n\n")
                    }
                    tvList.text = sb.toString()

                    if (!isFirstLoad) {
                        for (dc in snapshots.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                // 3. CONTROLLO CHI L'HA SCRITTO
                                val senderId = dc.document.getString("senderId")

                                // Se l'ID del mittente è DIVERSO dal mio ID, allora è un'altra persona!
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

        // 4. AGGIUNTA (Producer)
        btnAdd.setOnClickListener {
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            val age = etAge.text.toString()

            if (name.isNotEmpty() && surname.isNotEmpty() && age.isNotEmpty()) {
                val personMap = hashMapOf(
                    "name" to name,
                    "surname" to surname,
                    "age" to age.toIntOrNull(),
                    "senderId" to myDeviceId // FIRMIAMO IL MESSAGGIO!
                )

                db.collection("people").add(personMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Inviato!", Toast.LENGTH_SHORT).show()
                        etName.text.clear()
                        etSurname.text.clear()
                        etAge.text.clear()
                    }
            } else {
                Toast.makeText(this, "Compila tutto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Funzione per generare un ID univoco che resta salvato nel telefono
// NEW
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
            val name = "Aggiornamenti Persone"
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
            .setSmallIcon(android.R.drawable.ic_menu_add) // O usa R.drawable.ic_person_add se l'hai creato
            .setContentTitle("Nuovo Utente!")
            .setContentText("$name $surname aggiunto da un altro dispositivo.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
