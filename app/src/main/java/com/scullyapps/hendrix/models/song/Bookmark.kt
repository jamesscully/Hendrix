package com.scullyapps.hendrix.models.song

import android.database.Cursor

class Bookmark(hash : String = "error_hash", time : Int = 0, caption : String = "caption") {
    private val TAG: String = "Bookmark";

    // constructor for song objects
    constructor(song : Song, time: Int, caption: String) : this(song.calculateMD5(), time, caption)

    var timestamp : Int = time
    var caption : String = caption

    // hash for what song we're supposed to be attached to
    var hash : String = hash

    override fun toString(): String {
        return "Bookmark($hash - $timestamp - $caption)"
    }

    companion object {
        fun fromCursor(cursor: Cursor) : Bookmark {
            return Bookmark(
                cursor.getString(0),
                cursor.getInt(1),
                cursor.getString(2)
            )
        }
    }
}