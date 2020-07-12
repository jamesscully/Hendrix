package com.scullyapps.hendrix.activities.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import com.scullyapps.hendrix.models.song.Bookmark
import com.scullyapps.hendrix.models.song.Song

class PlayViewModel : ViewModel() {
    private val TAG: String = "PlayViewModel"

    var timeleft_text = ""
    var song : Song = Song()
    var bookmarks : ArrayList<Bookmark>? = null


    private fun loadBookmarks() {

    }
}