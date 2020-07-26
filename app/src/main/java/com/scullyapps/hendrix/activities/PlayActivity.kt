package com.scullyapps.hendrix.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
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
import com.scullyapps.hendrix.services.PlayerState
import com.scullyapps.hendrix.services.SoundService
import kotlinx.android.synthetic.main.activity_play.*
import kotlin.concurrent.fixedRateTimer

class PlayActivity : AppCompatActivity() {

    val TAG = "PlayActivity"

    private val model: PlayViewModel by viewModels<PlayViewModel>()

    private lateinit var service: SoundService
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }

        override fun onServiceConnected(name: ComponentName?, bindingService: IBinder?) {
            val binder = bindingService as SoundService.SoundBinder
            service = binder.getService()
            bound = true

            Log.d(TAG, "Servicebound!")

            if(service.song == null) {
                service.loadSong(model.song)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        startAndBind()
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        bound = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        setSupportActionBar(findViewById(R.id.toolbar))

        model.playbar = playbarDisplay

        setupObservers()

        // Bundle should contain song
        if (intent.extras != null) {
            if(intent.extras.containsKey("song")) {
                model.song = intent?.extras?.getSerializable("song") as Song
                    Log.d(TAG, "Retrieved Song: $model.song")
            }
        } else {
            Log.e(TAG, "Player was started with no bundle; no songs!")
            finishActivity(0)
        }

        fixedRateTimer("updateprog", true, 0, 1000) {
            this@PlayActivity.runOnUiThread {

                // return if we're not bound
                if(!bound)
                    return@runOnUiThread

                if(service.state == PlayerState.PLAYING) {
                    model.playbar.time = service.getPosition()
                    model.playbar.duration = service.getDuration()
                    updateUI()
                }
            }
        }

        Log.d(TAG,"Song has MD5 sum: " + model.song.calculateMD5())

        setupButtons()
        updateUI()

        model.playbar.invalidate()
    }

    /*
        Observers can be set-up independently; place here
     */
    fun setupObservers() {
        model.timeleftText.observe(this, Observer {
            txt_play_timeleft.text = it
        })

        model.songinfoText.observe(this, Observer {
            txt_play_info.text = it
        })

        model.bookmarks.observe(this, Observer {
            model.playbar.setBookmarks(it)
        })
    }

    fun setupButtons() {

        // Play/pause button
        btn_play.setOnClickListener {
            // pressing play/pause means we _need_ the service running
            if(!bound) {
                startAndBind()
            }

            val drawable: Int

            // toggle
            if(service.state == PlayerState.PLAYING && bound) {
                drawable = R.drawable.ic_play
                service.pause()
            } else {
                drawable = R.drawable.ic_pause

                if(service.song != null) {
                    if(service.song!! != model.song) {
                        service.stop()
                        service.loadSong(model.song)
                        updateUI()
                    }
                }

                service.play()

                service.showNotification()
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
        val s = model.song

        model.songinfoText.value = "${s.artist} - ${s.title}"

        if(bound)
            setTimeLeft(service.getPosition())

        if(model.playbar.finishedMoving && bound) {
            service.seekTo(model.playbar.millisFromCursor())
            model.playbar.finishedMoving = false
            service.play()
        }

        model.playbar.invalidate()
    }

    private fun startAndBind() {
        Log.d(TAG, "Starting and binding")

        // curious if this will occur, leaving here
        if(bound)
            Log.w(TAG, "Warning: starting and binding whilst bound?")

        val intent = Intent(this, SoundService::class.java)
        startService(intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun setTimeLeft(t : Int) {
        val currentTime = Song.millisToTimestamp(t)
        val durationTime = Song.millisToTimestamp(model.player.player.duration)
        model.timeleftText.value = "$currentTime / $durationTime"
    }
}