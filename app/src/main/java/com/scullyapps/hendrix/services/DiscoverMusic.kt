package com.scullyapps.hendrix.services

import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import com.scullyapps.hendrix.data.song.Song
import java.io.File
import kotlin.system.measureTimeMillis


class DiscoverMusic : IntentService("DiscoverMusicService") {

    private val TAG : String = "DiscoverMusicService"

    override fun onHandleIntent(intent: Intent?) {
        try {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                Log.d(TAG, "Could not get external storage permissions!")
                this.stopSelf()
            }

            val timeExec = measureTimeMillis {

                File(Environment.getExternalStorageDirectory(), "Music").walkTopDown()
                    .filter {
                        // ensure we're only reading files with audio extensions
                        file -> Song.isValidAudioExt(file.extension) && file.isFile
                    }
                    .forEach {
                        val newSong = Song(it)
                        println(newSong)
                    }
            }

            Log.d(TAG, "Finished executing in ${timeExec / 1000F}s")

        } catch (e: InterruptedException) {
            Log.e(TAG, "Thread was interrupted!")
            Thread.currentThread().interrupt()
        }
    }
}