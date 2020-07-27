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
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.scullyapps.hendrix.R
import com.scullyapps.hendrix.activities.PlayActivity
import com.scullyapps.hendrix.models.song.Song
import com.scullyapps.hendrix.services.DiscoverMusic
import com.scullyapps.hendrix.ui.SongDisplay

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

        val updateUI = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d(TAG, "Received data")

                val songs = intent?.extras?.getSerializable("songs") as? ArrayList<Song>

                if(songs == null) {
                    return
                }

                for(i in songs) {
                    val add = SongDisplay(root.context, i)

                    val intent = Intent()
                    val bundle = Bundle()

                    intent.setClass(inflater.context, PlayActivity::class.java)
                    bundle.putSerializable("song", i)

                    intent.putExtras(bundle)

                    add.setOnClickListener {_ ->
                        startActivity(intent)
                    }

                    songsLayout.addView(add)
                }
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
