package com.scullyapps.hendrix.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.scullyapps.hendrix.R
import com.scullyapps.hendrix.ui.MusicDisplay
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)


        val favs = root.findViewById<LinearLayout>(R.id.layout_favourites)

        favs.addView(MusicDisplay(root.context, "Jimi Hendrix"))
        favs.addView(MusicDisplay(root.context, "Pantera Ozzfest 2001"))
        favs.addView(MusicDisplay(root.context, ""))
        favs.addView(MusicDisplay(root.context, "Test"))

        return root
    }
}