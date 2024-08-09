/*
 * Created by Samyak Kamble on8/9/24, 9:48 PM Copyright (c) 2024 . All rights reserved.
 * Last modified 8/9/24, 9:48 PM
 */

package com.samyak2403.arrowchatapp.Adapter



import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.samyak2403.arrowchatapp.ChatFragment
import com.samyak2403.arrowchatapp.StatusFragment

class PagerAdapter(
    @NonNull fm: FragmentManager,
    behavior: Int
) : FragmentPagerAdapter(fm, behavior) {

    private val tabCount: Int = behavior

    @NonNull
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ChatFragment()
            1 -> StatusFragment()
            2 -> CallFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getCount(): Int {
        return tabCount
    }
}
