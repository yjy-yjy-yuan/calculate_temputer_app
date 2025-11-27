package com.example.calculate_temputer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.calculate_temputer.ui.MainPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var topTabs: TabLayout

    private val pageTitles = listOf(
        R.string.tab_calculator,
        R.string.tab_weather,
        R.string.tab_profile
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupPagerAndNavigation()
    }

    private fun setupPagerAndNavigation() {
        viewPager = findViewById(R.id.main_view_pager)
        bottomNavigationView = findViewById(R.id.main_bottom_nav)
        topTabs = findViewById(R.id.main_top_tabs)

        val adapter = MainPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.itemCount

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val selectedItemId = when (position) {
                    0 -> R.id.menu_calculator
                    1 -> R.id.menu_weather
                    else -> R.id.menu_profile
                }
                if (bottomNavigationView.selectedItemId != selectedItemId) {
                    bottomNavigationView.selectedItemId = selectedItemId
                }
            }
        })

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_calculator -> viewPager.setCurrentItem(0, true)
                R.id.menu_weather -> viewPager.setCurrentItem(1, true)
                R.id.menu_profile -> viewPager.setCurrentItem(2, true)
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        TabLayoutMediator(topTabs, viewPager) { tab, position ->
            tab.text = getString(pageTitles[position])
        }.attach()
    }
}
