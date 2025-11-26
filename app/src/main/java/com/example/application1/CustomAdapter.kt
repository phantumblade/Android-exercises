package com.example.application1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// L'adapter riceve la lista di dati (ItemsViewModel) nel suo costruttore
class CustomAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // 1. Crea la "scatola" vuota per la riga (ViewHolder)
    // Questo metodo viene chiamato solo le prime volte, per creare le righe che vedi sullo schermo.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // "Gonfia" (crea) il layout della singola carta che abbiamo disegnato prima
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    // 2. Riempie la "scatola" con i dati giusti
    // Questo metodo viene chiamato ogni volta che scorri e una nuova riga deve apparire.
    // "Ricicla" la vista e ci mette dentro i nuovi dati.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Prende l'elemento dalla nostra lista di dati alla posizione 'position'
        val itemsViewModel = mList[position]

        // Imposta l'immagine e il testo usando i riferimenti tenuti dal ViewHolder
        holder.imageView.setImageResource(itemsViewModel.image)
        holder.textView.text = itemsViewModel.text
    }

    // 3. Dice alla RecyclerView quanti elementi ci sono in totale nella lista
    override fun getItemCount(): Int {
        return mList.size
    }

    // Classe interna (ViewHolder) che tiene i riferimenti ai componenti grafici della riga
    // In questo modo, non dobbiamo fare 'findViewById' ogni volta che scorriamo, risparmiando risorse.
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = ItemView.findViewById(R.id.card_imageview)
        val textView: TextView = ItemView.findViewById(R.id.card_textview)
    }
}
