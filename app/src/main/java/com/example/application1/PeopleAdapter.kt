package com.example.application1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PeopleAdapter(
    private var peopleList: List<PersonModel>,
    private val onDeleteClick: (String) -> Unit // Funzione che passeremo per cancellare
) : RecyclerView.Adapter<PeopleAdapter.PersonViewHolder>() {

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvPersonName)
        val tvAge: TextView = itemView.findViewById(R.id.tvPersonAge)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteSingle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = peopleList[position]
        holder.tvName.text = "${person.name} ${person.surname}"
        holder.tvAge.text = "Età: ${person.age}"

        // Quando clicchi il cestino, chiama la funzione passando l'ID
        holder.btnDelete.setOnClickListener {
            onDeleteClick(person.id)
        }
    }

    override fun getItemCount() = peopleList.size

    // Funzione per aggiornare i dati senza ricreare l'adapter
    fun updateData(newPeople: List<PersonModel>) {
        peopleList = newPeople
        notifyDataSetChanged()
    }
}
