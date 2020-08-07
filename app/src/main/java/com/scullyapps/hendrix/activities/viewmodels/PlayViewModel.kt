package com.scullyapps.hendrix.activities.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.scullyapps.hendrix.data.BookmarkDB
import com.scullyapps.hendrix.models.song.Bookmark
import com.scullyapps.hendrix.models.song.Song
import com.scullyapps.hendrix.ui.PlaybarDisplay
import com.scullyapps.hendrix.ui.sound.SoundPlayer
import kotlin.properties.Delegates

class PlayViewModel : ViewModel() {
    private val TAG: String = "PlayViewModel"

    var timeleftText = MutableLiveData<String>("00:00")
    var songinfoText = MutableLiveData<String>("Default Artist - Default Track")

    lateinit var player : SoundPlayer
    lateinit var playbar: PlaybarDisplay

    var song : Song by Delegates.observable(Song()) {
        property, oldValue, newValue ->

        if(oldValue != newValue) {
            loadBookmarks()
            loadSongInfo()
            loadTimeLeft()
            player = SoundPlayer(newValue)
        }
    }

    var bookmarks = MutableLiveData<ArrayList<Bookmark>>()

    private fun loadBookmarks() {
        bookmarks.value = BookmarkDB.getBookmarks(song)
    }

    private fun loadSongInfo() {
        songinfoText.value = "${song.artist} - ${song.title}"
    }

    // loads the default string, which will be 0 secs to max
    private fun loadTimeLeft() {
        timeleftText.value = "00:00 - ${Song.millisToTimestamp(song.duration)}"
    }
}