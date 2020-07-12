package com.scullyapps.hendrix.activities

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.scullyapps.hendrix.GlobalApp
import com.scullyapps.hendrix.R
import com.scullyapps.hendrix.activities.viewmodels.PlayViewModel
import com.scullyapps.hendrix.data.BookmarkDB
import com.scullyapps.hendrix.models.song.Bookmark
import com.scullyapps.hendrix.models.song.Song
import com.scullyapps.hendrix.ui.sound.PlayerState
import kotlinx.android.synthetic.main.activity_play.*
import kotlin.concurrent.fixedRateTimer

class PlayActivity : AppCompatActivity() {

    val TAG = "PlayActivity"

    private val model: PlayViewModel by viewModels<PlayViewModel>()

    // TODO move all data to ViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Bundle should contain song
        if (intent.extras != null) {
            if(intent.extras.containsKey("song")) {
                model.song = intent.extras.getSerializable("song") as Song
                    Log.d(TAG, "Retrieved Song: $model.song")
            }
        } else {
            Log.e(TAG, "Player was started with no bundle; no songs!")
            finishActivity(0)
        }



        model.timeleftText.observe(this, Observer {
            txt_play_timeleft.text = it
        })

        model.songinfoText.observe(this, Observer {
            txt_play_info.text = it
        })

        model.bookmarks.observe(this, Observer {
            model.playbar.setBookmarks(it)
        })

        val TICK_TIME : Long = 1000
        fixedRateTimer("updateprog", true, 0, TICK_TIME) {
            this@PlayActivity.runOnUiThread {
                if(model.player.state == PlayerState.PLAYING) {
                    model.playbar.time = model.player.player.currentPosition
                    model.playbar.duration = model.player.player.duration
                    updateUI()
                }
            }
        }

        Log.d(TAG,"Song has MD5 sum: " + model.song.calculateMD5())

        setupButtons()
        updateUI()
    }

    fun setupButtons() {

        // Play/pause button
        btn_play.setOnClickListener {
            if(model.player.state == PlayerState.ERROR) {
                Log.e(TAG, "Player is not in a usable state. Fix!")
                return@setOnClickListener
            }

            val drawable: Int

            // toggle
            if(model.player.state == PlayerState.PLAYING) {
                drawable = R.drawable.ic_play
                model.player.pause()
            } else {
                drawable = R.drawable.ic_pause
                model.player.play()
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
            val time = model.player.player.currentPosition
            val hash = model.player.song.calculateMD5()
            val caption = "This is a test caption"

            val bookmark = Bookmark(hash, time, caption)

            BookmarkDB.createBookmark(bookmark)
        }
    }

    // Updates control UI from player (timeleft, artistname, etc)
    fun updateUI() {
        val s = model.player.song

        model.songinfoText.value = "${s.artist} - ${s.title}"
        setTimeLeft(model.player.player.currentPosition)

        if(model.playbar.finishedMoving) {
            model.player.player.seekTo(model.playbar.millisFromCursor())
            model.playbar.finishedMoving = false
            model.player.play()
        }

        model.playbar.time = model.player.player.currentPosition
        model.playbar.duration = model.player.player.duration

        model.playbar.invalidate()
    }


    private fun setTimeLeft(t : Int) {
        val currentTime = Song.millisToTimestamp(t)
        val durationTime = Song.millisToTimestamp(model.player.player.duration)
        model.timeleftText.value = "$currentTime / $durationTime"
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}