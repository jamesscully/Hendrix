package com.scullyapps.hendrix.services

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.math.MathUtils
import com.scullyapps.hendrix.R
import com.scullyapps.hendrix.models.song.Song
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.concurrent.fixedRateTimer

class SoundService : Service() {
    private val TAG: String = "SoundService";

    private val player : MediaPlayer = MediaPlayer()
    var song : Song? = null

    private val played = Stack<Song>()
    private val upNext = Stack<Song>()

    var state : PlayerState = PlayerState.STOPPED

    val binder = SoundBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service has been started!")

        // prepare notification channel ahead of time
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            makeNotificationChannel()

        fixedRateTimer("soundservice_timer", true, 0, 1000) {
            this@SoundService.run {
                if(state == PlayerState.PLAYING) {
                    showNotification()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "Service has been bound")
        return binder
    }

    inner class SoundBinder : Binder() {
        fun getService() : SoundService = this@SoundService
    }

    //
    // Playback Information
    //

    fun getPosition() : Int {
        return player.currentPosition
    }

    fun getDuration() : Int {
        return player.duration
    }

    fun getPercentage() : Float {
        val percent = (getDuration() / getDuration()).toFloat()
        return MathUtils.clamp(percent, 0F, 1F)
    }

    fun getCurrentTimestamp() : String {
        return Song.millisToTimestamp(getPosition())
    }

    fun getDurationTimestamp() : String {
        return Song.millisToTimestamp(getDuration())
    }

    //
    // Song loading methods
    //
    private fun loadFromFile(path: String) : Boolean {
        return try {
            player.reset()
            player.setDataSource(path)
            player.prepare()
            true
        } catch (exception: IOException) {
            Log.e(TAG, "Error loading song into player\n" + exception.stackTrace)
            false
        }
    }

    fun loadSong(song : Song) {
        // if load is successful, then change our current song
        if(loadFromFile(song.path)) {
            this.song = song
        }
    }

    fun addToQueue(song: Song) {
        upNext.push(song)
    }

    //
    // Playback methods
    //

    fun pause() {
        if(player.isPlaying) {
            Log.d(TAG, "Pausing song ${song}")
            player.pause()
            state = PlayerState.PAUSED
            showNotification()
        }
    }

    fun play() {
        // attempting to play null will result in disaster
        if(!player.isPlaying && song != null) {
            Log.d(TAG, "Playing song ${song}")
            player.start()
            state = PlayerState.PLAYING
            showNotification()
        }
    }

    fun stop() {
        player.stop()
        state = PlayerState.STOPPED
        showNotification()
    }

    fun next() {
        if(upNext.empty()) {
            Log.d(TAG, "Tried to skip fwd when no song exists, returning...")
            return
        }

        val next = upNext.pop()

        played.push(song)

        loadSong(next)
    }

    fun prev() {
        // if we have nothing to go to, ignore this call
        if(played.empty()) {
            Log.d(TAG, "Tried to skip backwd when no song exists, returning...")
            return
        }

        val next = played.pop()

        // add to queue
        upNext.push(song)

        loadSong(next)
    }

    fun seekTo(pos: Int) {
        player.seekTo(pos)
    }

    //
    // Notifications
    //

    private val CHANNEL_ID = "HendrixChannelID"

    // for now, we'll only need one notification to show playback
    private val NOTIF_ID = 133742

    private fun makeNotification() : Notification {
        val notification = NotificationCompat.Builder(this, "HendrixChannelID")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Hendrix is ${state.sentenceString}")
            .setContentText("${song?.artist} - ${song?.title} ${getCurrentTimestamp()} / ${getDurationTimestamp()}")
            .setOnlyAlertOnce(true)
            .build()
        return notification
    }

    fun hideNotification() {
        with(NotificationManagerCompat.from(this)) {
            cancel(NOTIF_ID)
        }
    }

    fun showNotification() {
        val notification = makeNotification()

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIF_ID, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun makeNotificationChannel() {
        val nChannelName = "HendrixChannel"
        val nChannelDesc = "HendrixDescription"
        val nImportance = NotificationManager.IMPORTANCE_LOW

        val channel = NotificationChannel(CHANNEL_ID, nChannelName, nImportance).apply {
            description = nChannelDesc
        }

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }
}
