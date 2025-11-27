package com.example.calculate_temputer.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.calculate_temputer.ui.calculator.CalculatorFragment
import com.example.calculate_temputer.ui.profile.ProfileFragment
import com.example.calculate_temputer.ui.weather.WeatherFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CalculatorFragment()
            1 -> WeatherFragment()
            else -> ProfileFragment()
        }
    }
}
