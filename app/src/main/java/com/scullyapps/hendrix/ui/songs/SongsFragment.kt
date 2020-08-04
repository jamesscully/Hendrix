package com.scullyapps.hendrix.ui.songs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scullyapps.hendrix.R
import com.scullyapps.hendrix.activities.PlayActivity
import com.scullyapps.hendrix.models.song.Song
import com.scullyapps.hendrix.services.DiscoverMusic

class SongsFragment : Fragment() {

    private val TAG: String = "SongsFragment"

    private lateinit var viewModel: SongsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_songs, container, false)
        val recycler = root.findViewById<RecyclerView>(R.id.song_list_holder)


        // intent for launch
        val intent = Intent().apply {
            setClass(inflater.context, PlayActivity::class.java)
        }

        // on click
        val adapter = SongsRAdapter() {song ->
            intent.putExtra("song", song)
            startActivity(intent)
        }
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(inflater.context)

        val updateUI = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d(TAG, "Received data")

                val songs = intent?.extras?.getSerializable("songs") as? ArrayList<Song> ?: return
                adapter.dataset.addAll(songs)
                adapter.notifyDataSetChanged()
            }
        }

        LocalBroadcastManager.getInstance(root.context).registerReceiver(updateUI, IntentFilter("receive-songs"))

        Intent(root.context, DiscoverMusic::class.java).also { intent ->
            activity?.startService(intent)
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SongsViewModel::class.java)
        // TODO: Use the ViewModel


    }

}
