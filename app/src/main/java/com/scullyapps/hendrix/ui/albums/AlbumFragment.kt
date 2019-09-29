package com.scullyapps.hendrix.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.scullyapps.hendrix.R

class AlbumFragment : Fragment() {

    private lateinit var albumViewModel: AlbumViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        albumViewModel =
            ViewModelProviders.of(this).get(AlbumViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_albums, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        albumViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}