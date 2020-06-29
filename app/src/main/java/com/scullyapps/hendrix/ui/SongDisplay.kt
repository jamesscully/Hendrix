package com.scullyapps.hendrix.ui

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.scullyapps.hendrix.R
import com.scullyapps.hendrix.models.song.Song
import kotlinx.android.synthetic.main.widget_songdisplay.view.*

class SongDisplay(context: Context, song: Song) : FrameLayout(context) {
    private val TAG: String = "SongDisplay";


    init {
        LayoutInflater.from(context).inflate(R.layout.widget_songdisplay, this, true)

        sd_song_name.text = song.title
        sd_artist_name.text = song.artist
        sd_song_length.text = song.getDuration()

        //if(song.artwork != null)
        //    sd_song_art.setImageBitmap(song.artwork)
    }

}