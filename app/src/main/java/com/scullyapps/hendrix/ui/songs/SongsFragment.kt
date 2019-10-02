package com.scullyapps.hendrix.ui.songs

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.scullyapps.hendrix.R
import com.scullyapps.hendrix.data.repos.SongRepository
import com.scullyapps.hendrix.ui.SongDisplay
import kotlinx.android.synthetic.main.fragment_songs.*
import kotlin.system.measureTimeMillis

class SongsFragment : Fragment() {

    private val TAG: String = "SongsFragment"

    companion object {
        fun newInstance() = SongsFragment()
    }

    private lateinit var viewModel: SongsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_songs, container, false)

        val songsLayout = root.findViewById<LinearLayout>(R.id.song_list_holder)

        val timed = measureTimeMillis {
            for (i in SongRepository.getAllSongs()) {
                songsLayout.addView(SongDisplay(root.context, i))
            }
        }

        Log.d(TAG, "Loading songs took ${timed / 1000}s")

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SongsViewModel::class.java)
        // TODO: Use the ViewModel



    }

}
