package com.scullyapps.hendrix

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.scullyapps.hendrix.data.song.Song

class PlayActivity : AppCompatActivity() {

    // create default song
    var song : Song = Song()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Bundle should contain song
        if (savedInstanceState != null) {
            if(savedInstanceState.containsKey("song"))
                song = savedInstanceState.getSerializable("song") as Song
        }



    }
}