package com.scullyapps.hendrix

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.scullyapps.hendrix.data.song.Song
import com.scullyapps.hendrix.ui.PlaybarDisplay
import com.scullyapps.hendrix.ui.sound.PlayerState
import com.scullyapps.hendrix.ui.sound.SoundPlayer
import kotlinx.android.synthetic.main.activity_play.*
import kotlin.concurrent.fixedRateTimer

class PlayActivity : AppCompatActivity() {

    val TAG = "PlayActivity"

    // create default song
    var song : Song = Song()

    lateinit var player : SoundPlayer
    lateinit var playbar: PlaybarDisplay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        setSupportActionBar(findViewById(R.id.toolbar))

        val TICK_TIME : Long = 1000

        // this runs a timer that updates both the progress bar and timeleft every second.
        fixedRateTimer("updateprog", true, 0, TICK_TIME) {
            this@PlayActivity.runOnUiThread {
                if(player.state == PlayerState.PLAYING) {
                    setTimeLeft(player.player.currentPosition)
                }
            }
        }

        // Bundle should contain song
        if (intent.extras != null) {
            if(intent.extras.containsKey("song")) {
                song = intent.extras.getSerializable("song") as Song
                Log.d(TAG, "Retrieved Song: $song")
                player = SoundPlayer(song)
            }
        } else {
            Log.e(TAG, "Player was started with no bundle; no songs!")
        }

        playbar = playbarDisplay

        setupButtons()
        updateUI()
    }

    fun setupButtons() {
        btn_play.setOnClickListener {

            if(player.state == PlayerState.ERROR) {
                Log.e(TAG, "Player is not in a usable state. Fix!")
                return@setOnClickListener
            }

            // toggle; use small text (to preserve layout for initial testing)
            if(player.state == PlayerState.PLAYING) {
                btn_play.text = "Play"
                player.pause()
            } else {
                btn_play.text = "Paus"
                player.play()
            }

            updateUI()
        }
    }

    // Updates control UI from player (timeleft, artistname, etc)
    fun updateUI() {
        val s = player.song

        txt_play_info.text = "${s.artist} - ${s.title}"
        setTimeLeft(player.player.currentPosition)

        playbar.time = player.player.currentPosition
        playbar.duration = player.player.duration

        playbar.plsdraw()
    }

    private fun setTimeLeft(t : Int) {
        val currentTime = Song.millisToTimestamp(t)
        val durationTime = Song.millisToTimestamp(player.player.duration)
        txt_play_timeleft.text = "$currentTime / $durationTime"
    }
}