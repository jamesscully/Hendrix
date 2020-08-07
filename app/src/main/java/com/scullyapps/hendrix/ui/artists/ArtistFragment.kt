package com.scullyapps.hendrix.ui.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.scullyapps.hendrix.R

class ArtistFragment : Fragment() {

    private lateinit var artistViewModel: ArtistViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        artistViewModel =
            ViewModelProviders.of(this).get(ArtistViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_artists, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)

        artistViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}