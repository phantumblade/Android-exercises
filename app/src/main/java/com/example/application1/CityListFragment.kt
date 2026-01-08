package com.example.application1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class CityListFragment: Fragment() {

    //definizione di un interfaccia per comunicare con l'activity
    interface OnCitySelectedListener {
        fun onCitySelected(cityName: String)
    }

    private var listener: OnCitySelectedListener? = null
    //onAttach: il Fragment si "aggancia" all'activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //verfiica che implmenenti l'interfaccaccorrettamente
        if (context is OnCitySelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context deve implementare OnCitySelectedListener")
        }
    }

    //caricamento del layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_city_list, container, false)
        val cities = arrayOf("Milano", "Roma", "Napoli", "Firenze", "Torino", "Genova", "Bologna",)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, cities)
        val listView = view.findViewById<ListView>(R.id.city_list)
        listView.adapter = adapter

        //Al click dell'elemeno ->
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = cities[position]
            //uso il listener per comunicarlo all'activity
            listener?.onCitySelected(selectedCity)
        }
        return view
    }

    //Sgancio del fragment
    override fun onDetach() {
        super.onDetach()
        listener = null

    }
}