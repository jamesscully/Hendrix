package com.scullyapps.hendrix.activities

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.scullyapps.hendrix.GlobalApp
import com.scullyapps.hendrix.R
import com.scullyapps.hendrix.activities.viewmodels.PlayViewModel
import com.scullyapps.hendrix.data.BookmarkDB
import com.scullyapps.hendrix.models.song.Bookmark
import com.scullyapps.hendrix.models.song.Song
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



    // TODO move all data to ViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Bundle should contain song
        if (intent.extras != null) {
            if(intent.extras.containsKey("song")) {
                song = intent.extras.getSerializable("song") as Song
                Log.d(TAG, "Retrieved Song: $song")
                player = SoundPlayer(song)
            }
        } else {
            Log.e(TAG, "Player was started with no bundle; no songs!")
            finishActivity(0)
        }

        // alias ui element
        playbar = playbarDisplay

        val bookmarks : ArrayList<Bookmark> = BookmarkDB.getBookmarks(song)
        playbar.setBookmarks(bookmarks)

        val TICK_TIME : Long = 1000
        fixedRateTimer("updateprog", true, 0, TICK_TIME) {
            this@PlayActivity.runOnUiThread {
                if(player.state == PlayerState.PLAYING) {
                    playbar.time = player.player.currentPosition
                    playbar.duration = player.player.duration
                    updateUI()
                }
            }
        }

        Log.d(TAG,"Song has MD5 sum: " + song.calculateMD5())

        setupButtons()
        updateUI()
    }

    fun setupButtons() {

        // Play/pause button
        btn_play.setOnClickListener {
            if(player.state == PlayerState.ERROR) {
                Log.e(TAG, "Player is not in a usable state. Fix!")
                return@setOnClickListener
            }

            val drawable: Int

            // toggle
            if(player.state == PlayerState.PLAYING) {
                drawable = R.drawable.ic_play
                player.pause()
            } else {
                drawable = R.drawable.ic_pause
                player.play()
            }

            // update play button
            btn_play.setImageDrawable(ContextCompat.getDrawable(
                GlobalApp.getAppContext(),
                drawable
            ))

            updateUI()
        }

        // Bookmark button
        btn_play_bookmark.setOnClickListener {
            val time = player.player.currentPosition
            val hash = player.song.calculateMD5()
            val caption = "This is a test caption"

            val bookmark = Bookmark(hash, time, caption)

            BookmarkDB.createBookmark(bookmark)
        }
    }

    // Updates control UI from player (timeleft, artistname, etc)
    fun updateUI() {
        val s = player.song

        txt_play_info.text = "${s.artist} - ${s.title}"
        setTimeLeft(player.player.currentPosition)

        if(playbar.finishedMoving) {
            player.player.seekTo(playbar.millisFromCursor())
            playbar.finishedMoving = false
            player.play()
        }

        playbar.time = player.player.currentPosition
        playbar.duration = player.player.duration

        playbar.invalidate()
    }

    private fun setTimeLeft(t : Int) {
        val currentTime = Song.millisToTimestamp(t)
        val durationTime = Song.millisToTimestamp(player.player.duration)
        txt_play_timeleft.text = "$currentTime / $durationTime"
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}