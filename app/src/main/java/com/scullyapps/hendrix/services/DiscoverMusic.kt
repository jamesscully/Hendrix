package com.scullyapps.hendrix.services

import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.scullyapps.hendrix.GlobalApp
import com.scullyapps.hendrix.data.song.Song
import com.scullyapps.hendrix.ui.SongDisplay
import java.io.File
import kotlin.system.measureTimeMillis


class DiscoverMusic : IntentService("DiscoverMusicService") {
    private val TAG : String = "DiscoverMusicService"

    override fun onHandleIntent(intent: Intent?) {
        val cResolver = GlobalApp.getAppContext().contentResolver
        val cursor = cResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)
        val songs = ArrayList<Song>()

        // if we have no music
        if(cursor == null) {
            return
        }

        cursor.moveToFirst()

        // add all songs into array; these are serializable
        while(cursor.moveToNext()) {
            // if file is not music (ret. 0) then skip
            if(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) == 0)
                continue

            songs.add(Song(cursor))
        }

        val intent = Intent()

        // broadcast our data to receiver
        intent.setAction("receive-songs")
        intent.putExtra("songs", songs)

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}