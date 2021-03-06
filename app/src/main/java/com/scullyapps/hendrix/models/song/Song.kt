package com.scullyapps.hendrix.models.song

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.scullyapps.hendrix.data.MD5
import java.io.File
import java.io.Serializable


@kotlinx.serialization.Serializable
class Song : Serializable{

    private val TAG: String = "Song"

    var id       : Int = -1
    var path     : String = "null"

    var title    : String = "Unknown"
    var artist   : String = "Unknown"

    var albumName : String = "Unknown"
    var albumID : Int = -1

    var genre    : String = "Unknown"
    var duration : Int = 0


    val timestamp get() = millisToTimestamp(duration)

//    var artwork : Bitmap? = null

    constructor() {
    }

    constructor(cursor : Cursor) {
        id        = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
        path      = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))

        title     = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
        artist    = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))

        albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
        albumID   = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

        //genre     = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.))

        duration  = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
    }


    fun getDuration() : String {
        return millisToTimestamp(duration)
    }

    fun getURI() : Uri? {
        return Uri.fromFile(File(path))
    }

    override fun toString(): String {
        // The Weeknd - Starboy 03:15 HipHop

        return "$artist - $title ${millisToTimestamp(duration)} \n$"
    }


    // hash functions
    fun calculateMD5() : String {
        return MD5.calculateMD5(File(path))
    }

    fun compareMD5(md5 : String) : Boolean {
        return md5 == calculateMD5()
    }

    override fun equals(other: Any?): Boolean {
        if(other !is Song)
            return false

        return this.path == other.path
    }

    // "static" methods
    companion object {
        fun isValidAudioExt(fileExt: String): Boolean {
            val acceptedFileTypes: Array<String> = arrayOf("mp3", "m4a")
            return acceptedFileTypes.contains(fileExt)
        }

        // takes in duration as milliseconds, returns timestamp i.e. 13:37 / 1:13:37
        fun millisToTimestamp(duration : Int) : String {

            // convert from millis to seconds
            var dur = duration / 1000
            var hours : Int; var mins : Int; var secs : Int

            secs = dur % 60
            mins = (dur - secs) / 60

            if(mins >= 60) {
                hours = mins / 60
                mins %= 60

                return ("${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}")
            }

            return "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        }
    }

}