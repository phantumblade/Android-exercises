package com.example.application1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// Implementiamo l'interfaccia definita nel Fragment della lista
class MasterDetailActivity : AppCompatActivity(), CityListFragment.OnCitySelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_detail)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.master_fragment_container, CityListFragment())
                // Diamo un TAG al fragment di dettaglio per ritrovarlo dopo
                .replace(R.id.detail_fragment_container, CityDetailFragment(), "DETAIL_FRAGMENT")
                .commit()
        }
    }

    // Questo metodo scatta quando clicchi sulla lista
    override fun onCitySelected(cityName: String) {
        // 1. Cerchiamo il Fragment di destra che è già attivo
        val detailFragment = supportFragmentManager.findFragmentByTag("DETAIL_FRAGMENT") as? CityDetailFragment

        // 2. Gli diciamo di aggiornarsi
        detailFragment?.updateContent(cityName)
    }
}
