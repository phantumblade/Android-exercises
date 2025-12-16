package com.example.application1

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.Date
class ShoppingListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingAdapter
    private val shoppingList = ArrayList<ShoppingItem>()
    private val database = Firebase.database
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        title = "Shopping List (Realtime DB)"

        //controllo sul login
        val currentUser = auth.currentUser
        if(currentUser == null) {
            Toast.makeText(this, "Esegui prima il Login!", Toast.LENGTH_LONG).show()
            finish() //per chiudere l'activity e tornare idneitro alla principale
            return
        }

        //setup della lista
        recyclerView = findViewById(R.id.shoppingRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ShoppingAdapter(shoppingList)
        recyclerView.adapter = adapter

        //Riferimento al DB -> shopping_list con ID_UTENTE
        //l'utente vedrà solo la la sua lista personale
        val myRef = database.getReference("shopping_list").child(currentUser.uid)

        //LETTURA DAL DB

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                shoppingList.clear() // vuota la lista in locale
                for (postSnapshot in dataSnapshot.children) {
                    //converte il JSON in oggetto ShoppingItem
                    val item = postSnapshot.getValue(ShoppingItem::class.java)
                    if (item != null) shoppingList.add(item)
                }
                adapter.notifyDataSetChanged() // serve per aggiornare la grafica con i dati nuovi
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ShoppingListActivity, "Errore DB: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        //SCRITTURA
        findViewById<FloatingActionButton>(R.id.fabAddItem).setOnClickListener {
            mostraDialogInput(myRef)
        }
    }

    private fun mostraDialogInput(dbRef: com.google.firebase.database.DatabaseReference) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cosa vuoi comprare?")

        val input = EditText(this)
        input.hint = "Es. Latte, Uova..."
        builder.setView(input)

        builder.setPositiveButton("Aggiungi") { _, _ ->
            val text = input.text.toString()
            if (text.isNotEmpty()) {
                // 1. Crea una chiave univoca
                val key = text.replace(Regex("[.#$\\[\\]]"), "")

                val newItem = ShoppingItem(key, text, Date().toString())

                // Scriviamo direttamente al path del nome
                dbRef.child(key).setValue(newItem)
            }
        }
        builder.setNegativeButton("Annulla") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}