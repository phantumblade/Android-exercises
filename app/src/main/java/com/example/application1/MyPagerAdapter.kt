package com.example.application1

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    // Quante pagine vuoi? 3
    override fun getItemCount(): Int {
        return 3
    }

    // Quale pagina mostrare alla posizione 0, 1, 2?
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Fragment1() // Assicurati che si chiami come il file che hai creato
            1 -> Fragment2()
            2 -> Fragment3()
            else -> Fragment1()
        }
    }
}
