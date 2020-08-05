package com.scullyapps.hendrix.models.artist

import android.database.Cursor
import android.provider.MediaStore


data class Artist(
    var name : String = "Default Artist",
    var nTracks : Int = -1,
    var nAlbums : Int = -1
) {

    constructor(cursor: Cursor) : this() {
        this.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST))
        this.nTracks = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS))
        this.nAlbums = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS))
    }

    override fun toString(): String {
        return "Artist: $name (ntracks $nTracks) (nalbums $nAlbums)"
    }
}