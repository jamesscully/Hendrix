package com.scullyapps.hendrix.ui

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.scullyapps.hendrix.R
import com.scullyapps.hendrix.data.song.Song
import kotlinx.android.synthetic.main.widget_musicdisplay.view.*
import kotlinx.android.synthetic.main.widget_songdisplay.view.*

class SongDisplay(context: Context, song: Song) : FrameLayout(context) {
    private val TAG: String = "SongDisplay";


    init {
        LayoutInflater.from(context).inflate(R.layout.widget_songdisplay, this, true)
        sd_song_name.setText(song.title)
        sd_artist_name.setText(song.artist)

        sd_song_length.setText(song.getDuration())

        //if(song.artwork != null)
        //    sd_song_art.setImageBitmap(song.artwork)
    }

}