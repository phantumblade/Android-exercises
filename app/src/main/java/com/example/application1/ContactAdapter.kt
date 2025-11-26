package com.example.application1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// CORREZIONE QUI: La classe deve iniziare su una riga nuova, senza "import" davanti
class ContactAdapter(
    private val listaDati: List<Contatto>,
    private val onRowClick: (Contatto) -> Unit
) : RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    // 1. Crea la riga vuota (Layout)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return MyViewHolder(view)
    }

    // 2. Riempie la riga con i dati
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val persona = listaDati[position]
        holder.bind(persona, onRowClick)
    }

    // 3. Conta quanti elementi ci sono
    override fun getItemCount(): Int {
        return listaDati.size
    }

    // Classe interna che tiene i riferimenti ai componenti grafici
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.img_foto)
        val txtNome: TextView = itemView.findViewById(R.id.txt_nome_completo)
        val txtEta: TextView = itemView.findViewById(R.id.txt_eta)

        fun bind(contatto: Contatto, clickListener: (Contatto) -> Unit) {
            txtNome.text = "${contatto.nome} ${contatto.cognome}"
            txtEta.text = "${contatto.eta} anni"
            img.setImageResource(contatto.fotoResId)

            // Gestisce il click su tutta la riga
            itemView.setOnClickListener {
                clickListener(contatto)
            }
        }
    }
}
