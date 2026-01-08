package com.example.application1

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Modello Dati Utente per la lista
data class UserItem(
    val uid: String = "",
    val email: String = "",
    var unreadCount: Int = 0 // Campo speciale per la notifica locale
)

class UsersListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val userList = mutableListOf<UserItem>()
    private lateinit var adapter: UserAdapter

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val myUid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)

        // Configura la lista
        recyclerView = findViewById(R.id.recyclerUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UserAdapter(userList) { selectedUser ->
            // AL CLICK: Apri la chat e azzera il conteggio (opzionale, lo vedremo dopo)
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("receiverUid", selectedUser.uid)
            intent.putExtra("receiverName", selectedUser.email) // Usiamo la mail come nome
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Avvia il caricamento
        if (myUid != null) {
            fetchUsersAndCheckNotifications()
        }
    }

    private fun fetchUsersAndCheckNotifications() {
        // 1. Scarica TUTTI gli utenti
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                val tempUsers = mutableListOf<UserItem>()

                for (document in result) {
                    val uid = document.getString("uid") ?: document.id
                    val email = document.getString("email") ?: "No Email"

                    // Escludi me stesso dalla lista
                    if (uid != myUid) {
                        tempUsers.add(UserItem(uid, email, 0))
                    }
                }

                // Aggiungiamo gli utenti alla lista principale
                userList.addAll(tempUsers)
                adapter.notifyDataSetChanged()

                // 2. ORA LA PARTE "SMART": Controllo i messaggi non letti per ogni utente
                checkForUnreadMessages()
            }
    }

    private fun checkForUnreadMessages() {
        // Per ogni amico nella lista, controllo se mi ha scritto messaggi che non ho letto
        // (Nota: in un'app reale si userebbe un campo "read: false" nel messaggio)

        for ((index, user) in userList.withIndex()) {

            // Costruisco l'ID della stanza chat (A_B o B_A)
            val list = listOf(myUid!!, user.uid).sorted()
            val chatRoomId = "${list[0]}_${list[1]}"

            // Ascolto la chat room per contare i messaggi NON miei
            db.collection("chats").document(chatRoomId)
                .collection("messages")
                .whereNotEqualTo("senderId", myUid) // Prendo solo i messaggi che ha scritto LUI
                // .whereEqualTo("read", false) // Se avessimo implementato il campo 'read' useremmo questo
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener

                    if (snapshot != null) {
                        // In questo esempio semplificato, contiamo TUTTI i messaggi ricevuti.
                        // Per farlo preciso servirebbe aggiungere il campo "read = false" quando si invia il messaggio
                        // e settarlo a "true" quando si apre la chat.
                        // Qui simuliamo contando gli ultimi arrivati (es. ultimi 5 minuti) o semplicemente il totale per demo.

                        val count = snapshot.size() // Per ora mostra il totale messaggi ricevuti da lui

                        // Aggiorniamo la lista solo se il numero cambia
                        if (user.unreadCount != count) {
                            user.unreadCount = count
                            adapter.notifyItemChanged(index) // Rinfresca solo quella riga
                        }
                    }
                }
        }
    }

    // --- ADAPTER ---
    // --- ADAPTER AGGIORNATO CON GRAFICA MIGLIORE ---
    class UserAdapter(
        private val users: List<UserItem>,
        private val onClick: (UserItem) -> Unit
    ) : RecyclerView.Adapter<UserAdapter.Holder>() {

        class Holder(v: View) : RecyclerView.ViewHolder(v) {
            // Colleghiamo i nuovi ID del layout personalizzato
            val textEmail: TextView = v.findViewById(R.id.tvUserEmail)
            val textBadge: TextView = v.findViewById(R.id.tvUnreadBadge)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            // USIAMO IL NUOVO LAYOUT CARD
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_card, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val user = users[position]

            // Impostiamo l'email
            holder.textEmail.text = user.email

            // Gestione del pallino rosso
            if (user.unreadCount > 0) {
                holder.textBadge.visibility = View.VISIBLE
                holder.textBadge.text = "🔴 ${user.unreadCount} nuovi messaggi"
            } else {
                holder.textBadge.visibility = View.GONE
            }

            // Impostiamo il click su tutta la card
            holder.itemView.setOnClickListener { onClick(user) }
        }

        override fun getItemCount() = users.size
    }
}
