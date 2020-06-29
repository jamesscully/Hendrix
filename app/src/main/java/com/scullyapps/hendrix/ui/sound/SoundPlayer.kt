package com.scullyapps.hendrix.ui.sound

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.scullyapps.hendrix.models.song.Song
import kotlinx.io.IOException
import java.lang.IllegalArgumentException
import java.util.*

enum class PlayerState {
    PLAYING,
    PAUSED,
    STOPPED,
    ERROR
}

class SoundPlayer {
    private val TAG: String = "SoundPlayer";

    private val playedSongs : Stack<Song> = Stack()
    private val queuedSongs : Queue<Song> = LinkedList()

    var song = Song()

    var state = PlayerState.STOPPED

    var player = MediaPlayer()

    constructor(s : Song) {
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player = genMediaPlayer(s)
        song = s
    }

    fun play() {
        if(state != PlayerState.ERROR || state != PlayerState.PLAYING) {
            player.start()
            state = PlayerState.PLAYING
        }
    }

    private fun genMediaPlayer(s : Song) : MediaPlayer {
        val mp = MediaPlayer()

//        mp.setOnCompletionListener {_ ->
//            val ns = queuedSongs.poll()
//
//            if(ns == null) {
//                // handle null
//            }
//
//            mp.setNextMediaPlayer(genMediaPlayer(ns))
//            mp.start()
//        }

        try {
            mp.setDataSource(s.path)
            mp.prepare()
        } catch (e : IOException) {
            Log.e(TAG, e.toString())
            state = PlayerState.ERROR
        } catch (e : IllegalArgumentException) {
            Log.e(TAG, e.toString())
            state = PlayerState.ERROR
        } finally {
            return mp
        }
    }

    fun stop() {
        if(state != PlayerState.ERROR || state != PlayerState.STOPPED) {
            player.stop()
            state = PlayerState.STOPPED
        }
    }

    fun pause() {
        if(state != PlayerState.ERROR || state != PlayerState.PLAYING) {
            player.pause()
            state = PlayerState.PAUSED
        }
    }

    fun next() {
        if(state != PlayerState.ERROR) {
            addToPlayed(song)
            stop()
            song = queuedSongs.poll()
            play()
        }
    }

    fun previous() {
        if(state != PlayerState.ERROR) {
            // keep reference to current song
            val temp = song

            stop()

            // get previous and play
            song = playedSongs.pop()
            addToQueue(temp)
            play()
        }
    }

    fun addToQueue(s : Song) {
        if(state != PlayerState.ERROR) {
            queuedSongs.add(s)
        }
    }

    fun addToPlayed(s : Song) {
        if(state != PlayerState.ERROR) {
            playedSongs.push(s)
        }
    }

}