package com.scullyapps.hendrix.models.repos

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import com.scullyapps.hendrix.GlobalApp
import com.scullyapps.hendrix.models.song.Song
import java.io.File


object SongRepository {
    private val TAG: String = "SongRepository"


    private lateinit var songURI : Uri

    private var songs : ArrayList<Song> = arrayListOf()


    init {

        if (ContextCompat.checkSelfPermission(GlobalApp.getAppContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d(TAG, "Could not get external storage permissions!")
        }

    }



    fun getAllSongs() : ArrayList<Song> {
        return songs
    }


}