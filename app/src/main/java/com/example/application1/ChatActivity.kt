package com.example.application1

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Modello del Messaggio
data class ChatMessage(
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = 0
)

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerChat: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton

    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    // Dati per la connessione
    private var chatRoomId: String? = null
    private var myUid: String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Recupero i dati passati dalla UsersListActivity
        val receiverName = intent.getStringExtra("receiverName") ?: "Chat"
        val receiverUid = intent.getStringExtra("receiverUid")

        myUid = FirebaseAuth.getInstance().currentUser?.uid

        // Imposto il titolo della barra in alto
        supportActionBar?.title = receiverName

        // 2. Calcolo l'ID della Stanza (Room ID)
        // Ordino gli ID alfabeticamente così A->B e B->A generano lo stesso ID
        if (myUid != null && receiverUid != null) {
            val list = listOf(myUid!!, receiverUid).sorted()
            chatRoomId = "${list[0]}_${list[1]}"
        }

        // Binding delle view
        recyclerChat = findViewById(R.id.recyclerChat)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        // Configuro la RecyclerView
        adapter = ChatAdapter(messageList, myUid ?: "")
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true // I messaggi partono dal basso
        recyclerChat.layoutManager = layoutManager
        recyclerChat.adapter = adapter

        // 3. ASCOLTO I MESSAGGI IN TEMPO REALE
        if (chatRoomId != null) {
            listenForMessages()
        }

        // 4. INVIO MESSAGGIO
        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty() && chatRoomId != null && myUid != null) {
                sendMessage(text)
            }
        }
    }

    private fun listenForMessages() {
        db.collection("chats").document(chatRoomId!!)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                if (snapshot != null) {
                    messageList.clear()
                    for (doc in snapshot.documents) {
                        val msg = doc.toObject(ChatMessage::class.java)
                        if (msg != null) {
                            messageList.add(msg)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    // Scrolla all'ultimo messaggio
                    if (messageList.isNotEmpty()) {
                        recyclerChat.scrollToPosition(messageList.size - 1)
                    }
                }
            }
    }

    private fun sendMessage(text: String) {
        val msg = ChatMessage(
            text = text,
            senderId = myUid!!,
            timestamp = System.currentTimeMillis()
        )

        // Scrivo nel database
        db.collection("chats").document(chatRoomId!!)
            .collection("messages")
            .add(msg)
            .addOnSuccessListener {
                etMessage.setText("") // Pulisco il campo solo se inviato con successo
            }
    }

    // --- ADAPTER PERSONALIZZATO PER LA GRAFICA ---
    class ChatAdapter(private val msgs: List<ChatMessage>, private val currentUserId: String) :
        RecyclerView.Adapter<ChatAdapter.MsgHolder>() {

        class MsgHolder(v: android.view.View) : RecyclerView.ViewHolder(v) {
            // In questo esempio creo la view via codice nel CreateViewHolder
            // Qui casto la view root che è un LinearLayout
            val layout = v as LinearLayout
            val textView = layout.getChildAt(0) as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgHolder {
            // COSTRUISCO LA GRAFICA DEL MESSAGGIO VIA CODICE
            // (Per evitarti di creare altri file XML)

            val layout = LinearLayout(parent.context)
            layout.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layout.orientation = LinearLayout.HORIZONTAL
            layout.setPadding(16, 8, 16, 8)

            val textView = TextView(parent.context)
            textView.textSize = 16f
            textView.setPadding(24, 16, 24, 16)
            textView.maxWidth = 800 // Limite larghezza bolla

            // Aggiungo ombra e angoli arrotondati (tramite background drawable di sistema o colore semplice)

            layout.addView(textView)
            return MsgHolder(layout)
        }

        override fun onBindViewHolder(holder: MsgHolder, position: Int) {
            val msg = msgs[position]

            // Formatto l'orario
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val time = sdf.format(java.util.Date(msg.timestamp))

            holder.textView.text = "${msg.text}\n$time"

            // Creiamo uno sfondo arrotondato dinamicamente (GradientDrawable)
            val shape = android.graphics.drawable.GradientDrawable()
            shape.cornerRadius = 30f // Arrotondamento angoli

            if (msg.senderId == currentUserId) {
                // === MESSAGGIO MIO (Destra, Verde) ===
                holder.layout.gravity = Gravity.END
                shape.setColor(Color.parseColor("#DCF8C6")) // Verde WhatsApp
                holder.textView.setTextColor(Color.BLACK)

                // Margini per staccarlo dal bordo destro
                val params = holder.textView.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(100, 0, 0, 0) // Spazio a sinistra per non farlo troppo largo
            } else {
                // === MESSAGGIO ALTRUI (Sinistra, Bianco) ===
                holder.layout.gravity = Gravity.START
                shape.setColor(Color.WHITE)
                holder.textView.setTextColor(Color.BLACK)

                // Margini per staccarlo dal bordo sinistro
                val params = holder.textView.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(0, 0, 100, 0) // Spazio a destra
            }

            // Applica lo sfondo creato alla TextView
            holder.textView.background = shape
        }


        override fun getItemCount() = msgs.size
    }
}
