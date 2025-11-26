package com.example.application1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TabActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        // Collega il tuo Adapter (MyPagerAdapter.kt)
        viewPager.adapter = MyPagerAdapter(this)

        // Collega le Tab al ViewPager e dai i nomi
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Primo"
                1 -> tab.text = "Secondo"
                2 -> tab.text = "Terzo"
            }
        }.attach()
    }
}
