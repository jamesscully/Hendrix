package com.scullyapps.hendrix

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        playbar = playbarDisplay

        val bookmarks : ArrayList<Bookmark> = BookmarkDB.getBookmarks(song)

        playbar.setBookmarks(bookmarks)


        val TICK_TIME : Long = 1000

        // this runs a timer that updates both the progress bar and timeleft every second.
        fixedRateTimer("updateprog", true, 0, TICK_TIME) {
            this@PlayActivity.runOnUiThread {
                if(player.state == PlayerState.PLAYING) {
                    setTimeLeft(player.player.currentPosition)
                    playbar.time = player.player.currentPosition
                    playbar.duration = player.player.duration
                    playbar.invalidate()
                }
            }
        }



        Log.d(TAG,"Song has MD5 sum: " + song.calculateMD5())

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
                btn_play.setImageDrawable(ContextCompat.getDrawable(GlobalApp.getAppContext(), R.drawable.ic_play))
                player.pause()
            } else {
                btn_play.setImageDrawable(ContextCompat.getDrawable(GlobalApp.getAppContext(), R.drawable.ic_pause))
                player.play()
            }

            updateUI()
        }



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
        playbar.onTouchEvent(event)

        if(playbar.finishedMoving) {
            player.player.seekTo(playbar.movedToMillis)
            playbar.finishedMoving = false
        }

        return super.onTouchEvent(event)
    }
}