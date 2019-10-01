package com.scullyapps.hendrix.services

import android.Manifest
import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.scullyapps.hendrix.data.song.Song
import java.io.File
import java.sql.Time
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureNanoTime
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

                File(Environment.getExternalStorageDirectory(), "Music").walkTopDown().forEach {
                    val mmr = MediaMetadataRetriever()

                    if (it.isFile && it.extension == "mp3") {
                        mmr.setDataSource(it.absolutePath)

                        var artistName =
                            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                        var songName =
                            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)

                    }
                }
            }

            Log.d(TAG, "Finished executing in ${timeExec / 1000F}s")

        } catch (e: InterruptedException) {
            Log.e(TAG, "Thread was interrupted!")
            Thread.currentThread().interrupt()
        }
    }
}